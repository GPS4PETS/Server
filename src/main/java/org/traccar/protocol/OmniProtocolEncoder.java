/*
 * Copyright 2020 Anton Tananaev (anton@traccar.org)
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

import org.traccar.StringProtocolEncoder;
import org.traccar.model.Command;
import org.traccar.Protocol;

public class OmniProtocolEncoder extends StringProtocolEncoder {

    public OmniProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private Object formatCommand(Command command, String content) {
        String uniqueId = getUniqueId(command.getDeviceId());
        String result = String.format("*TRAS,OM,%s,%s#", uniqueId, content);
        result += "\r\n";
        return result;
    }

    @Override
    protected Object encodeCommand(Command command) {
        return switch (command.getType()) {
            case Command.TYPE_REBOOT_DEVICE ->
                formatCommand(command, "Centigrade0,1");
            case Command.TYPE_POWER_OFF ->
                formatCommand(command, "Centigrade0,2");
                case Command.TYPE_FACTORY_RESET ->
                formatCommand(command, "Centigrade0,3");
            case Command.TYPE_OMNISETUP ->
                formatCommand(command, "S1,240,%s,%s,%s,5,%s,%s", Command.KEY_FREQUENCY, Command.KEY_MOTIONSLEEP, Command.KEY_HOMEWIFI, Command.KEY_HOMEWIFIMAC);
            default -> null;
        };
    }
}
