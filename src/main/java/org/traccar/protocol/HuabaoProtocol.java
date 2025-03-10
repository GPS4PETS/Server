/*
 * Copyright 2015 - 2024 Anton Tananaev (anton@traccar.org)
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

import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.config.Config;
import org.traccar.model.Command;

import jakarta.inject.Inject;

public class HuabaoProtocol extends BaseProtocol {

    @Inject
    public HuabaoProtocol(Config config) {
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_PERIODIC_ORIG,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_POSITION_PERIODIC_STATIC,
                Command.TYPE_ALARM_ARM,
                Command.TYPE_ALARM_DISARM,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_BUZZER_ON,
                Command.TYPE_BUZZER_OFF,
                Command.TYPE_BUZZER_DURATION,
                Command.TYPE_LIGHT_ON,
                Command.TYPE_LIGHT_OFF,
                Command.TYPE_LIGHT_DURATION,
                Command.TYPE_LIVEMODE_ON,
                Command.TYPE_LIVEMODE_OFF,
                Command.TYPE_SET_APN,
                Command.TYPE_CORNER_RADIUS,
                Command.TYPE_TRANSPARENT,
                Command.TYPE_TRANSPARENT_SER);
        addServer(new TrackerServer(config, getName(), false) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline, Config config) {
                pipeline.addLast(new HuabaoFrameEncoder());
                pipeline.addLast(new HuabaoFrameDecoder());
                pipeline.addLast(new HuabaoProtocolEncoder(HuabaoProtocol.this));
                pipeline.addLast(new HuabaoProtocolDecoder(HuabaoProtocol.this));
            }
        });
    }

}
