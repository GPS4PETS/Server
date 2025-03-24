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

 /*
1.3.4S 1（Set functional parameters）

Server->Locator *TRAS,OM,123456789123456,S1,240,300,5,1,5,1,DD:59:DC:F1:18:0B#<LF>
1 Heartbeat interval (default 240 cannot be set)
2 WIFI reporting interval (>=60S <24H)
3 GPS reporting interval (>=0S <24H)
4 Mobile detection switch 1 turn on 0 off. After turning on, the locator will automatically
  turn off the tracking if there is no movement for ten minutes. Regularly report 45min for
  an update.
5 Low power alarm threshold set to "0" means that low power does not alarm range <100
6 Whether to turn on home WIFI. After turning on, the locator will find the home WiFi and stop reporting the
  location data.
7 Set the home WIFI address. When the home WiFi address is set to turn on 8, the locator will turn off the
  location report.

Locator->Server *TRAR,OM,123456789123456,S1,240,300,5,1,5,1,DD:59:DC:F1:18:0B#<LF>
1 Heartbeat interval
2 WIFI reporting interval
3 GPS tracking interval
4 Mobile detection switch status 1 on 0 off once
5 Low power alarm threshold
6 Whether to turn on home WIFI.
7 Set the home WIFI address.
*/
package org.traccar.protocol;

import org.traccar.Protocol;
import org.traccar.StringProtocolEncoder;
import org.traccar.model.Command;

/*
1.3.4S 1（Set functional parameters）
Server->Locator *TRAS,OM,123456789123456,S1,240,300,5,1,5,1,DD:59:DC:F1:18:0B#<LF>
1 Heartbeat interval (default 240 cannot be set)
2 WIFI reporting interval (>=60S <24H)
3 GPS reporting interval (>=0S <24H)
4 Mobile detection switch 1 turn on 0 off. After turning on, the locator will automatically
  turn off the tracking if there is no movement for ten minutes. Regularly report 45min for
  an update.
5 Low power alarm threshold set to "0" means that low power does not alarm range <100
6 Whether to turn on home WIFI. After turning on, the locator will find the home WiFi and stop reporting the
  location data.
7 Set the home WIFI address. When the home WiFi address is set to turn on 8, the locator will turn off the
  location report.

Locator->Server *TRAR,OM,123456789123456,S1,240,300,5,1,5,1,DD:59:DC:F1:18:0B#<LF>
1 Heartbeat interval
2 WIFI reporting interval
3 GPS tracking interval
4 Mobile detection switch status 1 on 0 off once
5 Low power alarm threshold
6 Whether to turn on home WIFI.
7 Set the home WIFI addres
 */

public class OmniProtocolEncoder extends StringProtocolEncoder {

    public OmniProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {

        return switch (command.getType()) {
            case Command.TYPE_POSITION_PERIODIC -> 
                formatCommand(command, "*TRAS,OM,123456789123456,S1,240,300,%s,1,5,0,DD:00:00:00:00:00#\r\n", Command.KEY_FREQUENCY);
            default -> null;
        };
    }

}
