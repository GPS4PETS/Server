/*
 * Copyright 2015 - 2020 Anton Tananaev (anton@traccar.org)
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

import io.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
// import org.traccar.api.resource.CommandResource;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.model.Command;

import jakarta.inject.Inject;

public class H02Protocol extends BaseProtocol {

    @Inject
    public H02Protocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_ALARM_ARM,
                Command.TYPE_ALARM_DISARM,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_LIGHT_ON,
                Command.TYPE_LIGHT_OFF,
                Command.TYPE_BUZZER_ON,
                Command.TYPE_BUZZER_OFF,
                Command.TYPE_LIVEMODE_ON,
                Command.TYPE_LIVEMODE_OFF,
                Command.TYPE_HOMEZONE1,
                Command.TYPE_HOMEZONE1_OFF
        );
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                int messageLength = config.getInteger(Keys.PROTOCOL_MESSAGE_LENGTH.withPrefix(getName()));
                pipeline.addLast(new H02FrameDecoder(messageLength));
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new H02ProtocolEncoder(H02Protocol.this));
                pipeline.addLast(new H02ProtocolDecoder(H02Protocol.this));
            }
        });
        addServer(new TrackerServer(config, getName(), true) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new H02ProtocolEncoder(H02Protocol.this));
                pipeline.addLast(new H02ProtocolDecoder(H02Protocol.this));
            }
        });
    }
}
