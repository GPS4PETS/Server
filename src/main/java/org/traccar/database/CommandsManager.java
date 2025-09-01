/*
 * Copyright 2017 - 2025 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 Andrey Kunitsyn (andrey@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.database;

import org.traccar.BaseProtocol;
import org.traccar.ServerManager;
import org.traccar.broadcast.BroadcastInterface;
import org.traccar.broadcast.BroadcastService;
import org.traccar.command.CommandSender;
import org.traccar.command.CommandSenderManager;
import org.traccar.model.Command;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.ObjectOperation;
import org.traccar.model.Position;
import org.traccar.model.QueuedCommand;
import org.traccar.session.ConnectionManager;
import org.traccar.session.DeviceSession;
import org.traccar.session.cache.CacheManager;
import org.traccar.sms.SmsManager;
import org.traccar.storage.Storage;
import org.traccar.storage.StorageException;
import org.traccar.storage.query.Columns;
import org.traccar.storage.query.Condition;
import org.traccar.storage.query.Order;
import org.traccar.storage.query.Request;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.traccar.config.Config;
import org.traccar.config.Keys;

import java.util.UUID;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth;

@Singleton
public class CommandsManager implements BroadcastInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsManager.class);
    @Inject
    private Config config;

    private final Storage storage;
    private final ServerManager serverManager;
    private final SmsManager smsManager;
    private final ConnectionManager connectionManager;
    private final BroadcastService broadcastService;
    private final NotificationManager notificationManager;
    private final CacheManager cacheManager;
    private final CommandSenderManager commandSenderManager;

    @Inject
    public CommandsManager(
            Storage storage, ServerManager serverManager, @Nullable SmsManager smsManager,
            ConnectionManager connectionManager, BroadcastService broadcastService,
            NotificationManager notificationManager, CacheManager cacheManager,
            CommandSenderManager commandSenderManager) {
        this.storage = storage;
        this.serverManager = serverManager;
        this.smsManager = smsManager;
        this.connectionManager = connectionManager;
        this.broadcastService = broadcastService;
        this.notificationManager = notificationManager;
        this.cacheManager = cacheManager;
        this.commandSenderManager = commandSenderManager;
        broadcastService.registerListener(this);
    }

    public QueuedCommand sendCommand(Command command) throws Exception {
        long deviceId = command.getDeviceId();
        Device device = storage.getObject(Device.class, new Request(
                new Columns.All(), new Condition.Equals("id", deviceId)));
        Position position = storage.getObject(Position.class, new Request(
                new Columns.All(), new Condition.Equals("id", device.getPositionId())));
        BaseProtocol protocol = position != null ? serverManager.getProtocol(position.getProtocol()) : null;

        if (command.getTextChannel()) {
            if (smsManager == null) {
                throw new RuntimeException("SMS not configured");
            }
            if (position != null) {
                protocol.sendTextCommand(device.getPhone(), command);
            } else if (command.getType().equals(Command.TYPE_CUSTOM)) {
                smsManager.sendMessage(device.getPhone(), command.getString(Command.KEY_DATA), true);
            } else {
                throw new RuntimeException("Command " + command.getType() + " is not supported");
            }
        } else {
            CommandSender sender = commandSenderManager.getSender(device);
            if (sender != null) {
                sender.sendCommand(device, command);
            } else {
                DeviceSession deviceSession = connectionManager.getDeviceSession(deviceId);
                if (deviceSession != null && deviceSession.supportsLiveCommands()) {
                    deviceSession.sendCommand(command);
                } else if (!command.getBoolean(Command.KEY_NO_QUEUE)) {
                    QueuedCommand queuedCommand = QueuedCommand.fromCommand(command);
                    queuedCommand.setId(storage.addObject(queuedCommand, new Request(new Columns.Exclude("id"))));
                    broadcastService.updateCommand(true, deviceId);
                    return queuedCommand;
                } else {
                    if (!device.getModel().equals("OMNI")) {
                        throw new RuntimeException("Failed to send command to ID: " + device.getUniqueId());
                    } else {
                        String key = "";
                        String value = "";
                        if (command.getType().equals("liveModeOn")) {
                            key = "LOSTMode";
                            value = "1";
                        } else if (command.getType().equals("liveModeOff")) {
                            key = "LOSTMode";
                            value = "0";
                        } else if (command.getType() == "positionPeriodic") {
                            key = "gps_upTime";
                            value = "120";
                        }

                        String host = config.getString(Keys.MQTT_HOST);
                        Integer port = config.getInteger(Keys.MQTT_PORT);
                        String username = config.getString(Keys.MQTT_USER);
                        String password = config.getString(Keys.MQTT_PASSWORD);

                        String topic = "/sys/orrcfhwg/" + device.getUniqueId() + "/thing/service/property/set";
                        String payload = "{\"version\":\"1.0\",\"params\":{\""
                            + key + "\":" + value + "},\"method\":\"thing.service.property.set\"}";

                        Mqtt5SimpleAuth simpleAuth = Mqtt5SimpleAuth.builder().username(username)
                            .password(password.getBytes()).build();

                        Mqtt5AsyncClient client = Mqtt5Client.builder()
                            .identifier(UUID.randomUUID().toString())
                            .serverHost(host)
                            .simpleAuth(simpleAuth)
                            .buildAsync();

                        client.connect()
                            .thenCompose(connAck -> client.publishWith().topic(topic).payload(payload.getBytes()).send())
                            .thenCompose(publishResult -> client.disconnect());

                        LOGGER.info("MQTT SEND: TOPIC: {} JSON: {}", topic, payload);
                        //deviceSession.sendCommand(command);
                        throw new RuntimeException("Send command ok");
                    }
                }
            }
        }
        return null;
    }

    public Collection<Command> readQueuedCommands(long deviceId) {
        return readQueuedCommands(deviceId, Integer.MAX_VALUE);
    }

    public Collection<Command> readQueuedCommands(long deviceId, int count) {
        try {
            var commands = storage.getObjects(QueuedCommand.class, new Request(
                    new Columns.All(),
                    new Condition.Equals("deviceId", deviceId),
                    new Order("id", false, count)));
            Map<Event, Position> events = new HashMap<>();
            for (var command : commands) {
                storage.removeObject(QueuedCommand.class, new Request(
                        new Condition.Equals("id", command.getId())));

                Event event = new Event(Event.TYPE_QUEUED_COMMAND_SENT, command.getDeviceId());
                event.set("id", command.getId());
                events.put(event, null);
            }
            notificationManager.updateEvents(events);
            return commands.stream().map(QueuedCommand::toCommand).toList();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCommand(boolean local, long deviceId) {
        if (!local) {
            DeviceSession deviceSession = connectionManager.getDeviceSession(deviceId);
            if (deviceSession != null && deviceSession.supportsLiveCommands()) {
                for (Command command : readQueuedCommands(deviceId)) {
                    deviceSession.sendCommand(command);
                }
            }
        }
    }

    public void updateNotificationToken(long deviceId, String token) {
        var key = new Object();
        try {
            cacheManager.addDevice(deviceId, key);
            Device device = cacheManager.getObject(Device.class, deviceId);
            device.set("notificationTokens", token);
            storage.updateObject(device, new Request(
                    new Columns.Include("attributes"),
                    new Condition.Equals("id", deviceId)));
            cacheManager.invalidateObject(true, Device.class, deviceId, ObjectOperation.UPDATE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cacheManager.removeDevice(deviceId, key);
        }
    }

}
