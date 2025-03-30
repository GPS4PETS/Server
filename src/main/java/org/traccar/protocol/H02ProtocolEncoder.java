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

import java.util.Date;

public class H02ProtocolEncoder extends StringProtocolEncoder {

    private static final String MARKER = "HQ";

    public H02ProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private Object formatCommand(Date time, String uniqueId, boolean notime, String type, String... params) {

        StringBuilder result = new StringBuilder(
            String.format("*%s,%s,%s", MARKER, uniqueId, type));

        if (!notime) {
            result.append(",").append(String.format("%4$tH%4$tM%4$tS", time));
        }

        for (String param : params) {
            result.append(",").append(param);
        }

        result.append("#");

        return result.toString();
    }

    protected Object encodeCommand(Command command, Date time) {
        String uniqueId = getUniqueId(command.getDeviceId());
        String frequency = "120";

        return switch (command.getType()) {
            case Command.TYPE_ALARM_ARM -> formatCommand(time, uniqueId, false, "SCF", "0", "0");
            case Command.TYPE_ALARM_DISARM -> formatCommand(time, uniqueId, false, "SCF", "1", "1");
            case Command.TYPE_ENGINE_STOP -> formatCommand(time, uniqueId, false, "S20", "1", "1");
            case Command.TYPE_ENGINE_RESUME -> formatCommand(time, uniqueId, false, "S20", "1", "0");
            case Command.TYPE_POSITION_PERIODIC -> {
                frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                if (AttributeUtil.lookup(
                        getCacheManager(), Keys.PROTOCOL_ALTERNATIVE.withPrefix(getProtocolName()),
                        command.getDeviceId())) {
                    yield formatCommand(time, uniqueId, false, "D1", frequency);
                } else {
                    yield formatCommand(time, uniqueId, false, "S71", "22", frequency);
                }
            }
            case Command.TYPE_LIGHT_ON -> formatCommand(time, uniqueId, true, "X2", "062108", "1");
            case Command.TYPE_LIGHT_OFF -> formatCommand(time, uniqueId, true, "X2", "062108", "0");
            case Command.TYPE_BUZZER_ON -> formatCommand(time, uniqueId, true, "X1", "062108", "60");
            case Command.TYPE_BUZZER_OFF -> formatCommand(time, uniqueId, true, "X1", "062108", "0");
            case Command.TYPE_LIVEMODE_ON -> formatCommand(time, uniqueId, true, "X5", "062108", "5", "300");
            case Command.TYPE_LIVEMODE_OFF -> formatCommand(time, uniqueId, true, "X5", "062108", "5", "10");
            case Command.TYPE_HOMEZONE1 -> formatCommand(time, uniqueId, true, "CHWALL", "062108", "");
            case Command.TYPE_HOMEZONE1_OFF -> formatCommand(time, uniqueId, true, "DELWALL", "062108", "");
            default -> null;
        };
    }

    @Override
    protected Object encodeCommand(Command command) {
        return encodeCommand(command, new Date());
    }

}
