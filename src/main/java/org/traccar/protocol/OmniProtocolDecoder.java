/*
 * Copyright 2020 - 2023 Anton Tananaev (anton@traccar.org)
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
1.3.5 Dimension0 (GPS location report Instructions)
Locator->Server *TRAR,OM,123456789123456,Dimension0,N,2238.07773,W,11407.55384,6,1.49,1599206439#<LF>
1 Address sign N S is used to distinguish north latitude and south latitude.
2 Latitude data
3 Address sign E W is used to distinguish the east long and west long.
4 Amenity data
5 Number of GPS satellites
6 Horizontal accuracy factor
7 Timestamp
Server->Locator No answer
*/
package org.traccar.protocol;

import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.session.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

import java.util.Date;

public class OmniProtocolDecoder extends BaseProtocolDecoder {

    public OmniProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private void sendResponse(Channel channel, SocketAddress remoteAddress, Long deviceId) {
        if (channel != null) {
            StringBuilder response = new StringBuilder("*TRAS,OM,");
            response.append(deviceId);
            response.append(",Q0,200,");
            response.append(new Date());
            response.append("#");
            response.append("\r\n");
            channel.writeAndFlush(new NetworkMessage(response.toString(), remoteAddress));
        }
    }

    /* *TRAR,OM,123456789123456,Dimension0,N,2238.07773,W,11407.55384,6,1.49,1599206439# */
    private static final Pattern PATTERN_POSITION = new PatternBuilder()
            .text("*TRAR,OM,")                   // header
            .expression("([^,]+),")              // id
            .expression("Dimenson0,")            // Report Name
            .expression("([NS])")
            .number("(dd)(dd.d+)")               // latitude
            .expression("([EW])")
            .number("(d{2,3})(dd.d+),")          // longitude
            .number("(d+),")                     // satellites
            .number("F(d+),")                    // hdop
            .number("(d+)#")                     // timestamp
            .any()
            .compile();

    private Object decodePosition(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_POSITION, sentence);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));

        var sat  = parser.nextInt();
        position.set(Position.KEY_SATELLITES, sat);
        position.setValid(sat > 2);
        position.set(Position.KEY_HDOP, parser.nextDouble() * 1000);
        position.setTime(new Date(parser.nextInt()));

        return position;
    }

    /* *TRAR,OM,123456789123456,Q0,80,1101,1,20,8C:59:DC:F1:18:0B# */
    private static final Pattern PATTERN_REGISTER = new PatternBuilder()
            .text("*TRAR,OM,")                   // header
            .expression("([^,]+),")              // id
            .expression("Q0,")                   // Report Name
            .number("(d+),")                     // battery level
            .number("(d+),")                     // firmware version
            .number("(d+),")                     // product type 1: 1st Pet Locator 2: 2nd Pet Locator 3: Personal Locator 4: Car Locator
            .number("(d+),")                     // rssi
            .number("(([^,]+)#")                 // mac adress
            .any()
            .compile();

    private Object decodeRegister(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_POSITION, sentence);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));

        sendResponse(channel, remoteAddress, deviceSession.getDeviceId());

        return position;
    }


    /* *TRAR,OM,123456789123456,H0,A7670C,A011B13A7670ME,80,20,240,300,60,60,1,1,DD:59:DC:F1:18:0B, 5# */
    private static final Pattern PATTERN_HEARTBEAT = new PatternBuilder()
            .text("*TRAR,OM,")                   // header
            .expression("([^,]+),")              // id
            .text("H0,")                         // Report Name
            .expression("([^,]+),")              // communication modul
            .expression("([^,]+),")              // communication modul version
            .number("(d+)")                      // battery level
            .number("(d+),")                     // rssi
            .number("(d+),")                     // heartbeat interval
            .number("(d+),")                     // wifi reporting interval
            .number("(d+),")                     // tracking interval
            .number("(d+),")                     // mobile detection switch
            .number("(d+),")                     // moving state
            .expression("([^,]+),")              // home wifi mac
            .number("(d+),")                     // low bat alarm
            .any()
            .compile();

    private Object decodeHeartbeat(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_HEARTBEAT, sentence);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        parser.next();
        parser.next();
        position.set(Position.KEY_BATTERY_LEVEL, parser.nextInt());
        position.set(Position.KEY_RSSI, parser.nextInt());
        parser.next();
        parser.next();
        parser.next();
        parser.next();
        position.set(Position.KEY_MOTION, parser.nextInt());

        return position;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        if (sentence.indexOf(",Dimension0,") > 0) {
            return decodePosition(channel, remoteAddress, sentence);
        } else if (sentence.indexOf(",H0,") > 0) {
            return decodeHeartbeat(channel, remoteAddress, sentence);
        } else if (sentence.indexOf(",Q0,") > 0) {
            return decodeRegister(channel, remoteAddress, sentence);
        } else {
            return null;
        }
    }

}
