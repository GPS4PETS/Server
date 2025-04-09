/*
 * Copyright 2016 Gabor Somogyi (gabor.g.somogyi@gmail.com)
 * Copyright 2016 - 2019 Anton Tananaev (anton@traccar.org)
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

import org.traccar.Protocol;
import org.traccar.StringProtocolEncoder;
import org.traccar.config.Keys;
import org.traccar.helper.model.AttributeUtil;
import org.traccar.model.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class OsmAndProtocolEncoder extends StringProtocolEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsmAndProtocolEncoder.class);

    public OsmAndProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private Object formatCommand(Command command, Date time, String key, String value) {
        String result = "{\"topic\":\"/sys/orrcfhwg/" + getUniqueId(command.getDeviceId()) + 
            "/thing/service/property/set\",\"qos\":1,\"clientid\":\"" + 
            getUniqueId(command.getDeviceId()) + ",\"payload\":\"{\"version\":\"1.0\",\"params\":{\"" + key + "\":\"" + value + 
            "\"},\"method\":\"thing.service.property.set\"}\"}";

        return result;
    }

    protected Object encodeCommand(Command command, Date time) {
        return switch (command.getType()) {
            case Command.TYPE_POSITION_PERIODIC -> {
                String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                yield formatCommand(command, time, "gps_upTime", frequency);
            }
            case Command.TYPE_LIVEMODE_ON -> formatCommand(command, time, "LOSTMode", "1");
            case Command.TYPE_LIVEMODE_OFF -> formatCommand(command, time, "LOSTMode", "0");
            default -> null;
        };
    }

    @Override
    protected Object encodeCommand(Command command) {
        return encodeCommand(command, new Date());
    }

}
