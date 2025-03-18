/*
 * Copyright 2023 Anton Tananaev (anton@traccar.org)
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
package org.traccar.protocol;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.kafka.common.utils.ByteBufferInputStream;
import org.traccar.BaseMqttProtocolDecoder;
import org.traccar.Protocol;
import org.traccar.model.Position;
import org.traccar.session.DeviceSession;

import java.util.Date;

public class OmniProtocolDecoder extends BaseMqttProtocolDecoder {

    public OmniProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(DeviceSession deviceSession, MqttPublishMessage message) throws Exception {

        JsonObject json;
        try (ByteBufferInputStream inputStream = new ByteBufferInputStream(message.payload().nioBuffer())) {
            json = Json.createReader(inputStream).readObject();
        }

        Position position = new Position(getProtocolName());

        String type = json.getString("Method");
        switch (type) {
            case "Thing.event.heartbeat.post_reply":
                position.setDeviceId(deviceSession.getDeviceId());

                position.setValid(true);

                position.setTime(new Date((long) (json.getJsonNumber("Timestamp").doubleValue()) * 1000L));

                position.set(Position.KEY_BATTERY_LEVEL, (int) json.getJsonNumber("Bat_Vol").doubleValue());
                position.set(Position.KEY_VERSION_FW, json.getString("FirmBios"));
                position.set(Position.KEY_VERSION_HW, json.getString("Hardware"));
                position.set(Position.KEY_RSSI, json.getString("CSQ"));

                return position;
            case "thing.event.Global Positioning System.Post":
                position.setDeviceId(deviceSession.getDeviceId());

                position.setValid(true);

                position.setTime(new Date((long) (json.getJsonNumber("Timestamp").doubleValue()) * 1000L));

                JsonObject location = json.getJsonObject("Result");
                position.setLatitude((location.getJsonNumber("pos_N").doubleValue() / 100) * (location.getString("GEO_NS") == "N" ? 1 : (-1)));
                position.setLongitude((location.getJsonNumber("pos_E").doubleValue() / 100) * (location.getString("GEO_EW") == "W" ? 1 : (-1)));

                position.set(Position.KEY_HDOP, location.getJsonNumber("Hdop").doubleValue());
                position.set(Position.KEY_SATELLITES, (int) location.getJsonNumber("gpsNum").doubleValue());

                position.set(Position.KEY_IGNITION, json.getString("ign").equals("on"));

                return position;
            case "thing.event.Wifi.Post":
                // found wifi networks
            case "thing.event.Property.Post":
                position.setDeviceId(deviceSession.getDeviceId());

                position.setValid(true);

                position.setTime(new Date((long) (json.getJsonNumber("Timestamp").doubleValue()) * 1000L));

                if (json.getString("ChangeState") == "1") {
                    position.set(Position.KEY_MOTION, json.getString("NoMove"));
                    position.set(Position.ALARM_LOW_BATTERY, json.getString("Batstate"));
                }
                return position;
            default:
                return null;
        }
    }

}
