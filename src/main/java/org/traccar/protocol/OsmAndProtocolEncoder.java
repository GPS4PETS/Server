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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.Date;

public class OsmAndProtocolEncoder extends StringProtocolEncoder {

    public OsmAndProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private Object formatCommand(Command command, Date time, String key, String value) {
        StringBuilder result = new StringBuilder(
            String.format("{\"topic\":\"/sys/orrcfhwg/$s/thing/service/property/set\",\"qos\":1,\"clientid\":$s,\"payload\":\"{\"version\":\"1.0\",\"params\":{\"$a\":$s},\"method\":\"thing.service.property.set\"}\"}", getUniqueId(command.getDeviceId()), getUniqueId(command.getDeviceId()), key, value));

        String cmd = "/usr/bin/curl -u e32bffef9d42278f:gs9Cb9A9Cv4AdPE0iioRdj41MgAVosV5tT3VM7OkO0x6wF -X POST -H 'Content-Type: application/json' -d ";
        cmd += result.toString();
        cmd += " https:/emqx.gps4pets.de/api/v5/publish";

        /*
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https:/emqx.gps4pets.de/api/v5/publish"))
                .header("Authorization", "e32bffef9d42278f:gs9Cb9A9Cv4AdPE0iioRdj41MgAVosV5tT3VM7OkO0x6wF")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(result.toString()))
                .build();
            HttpClient http = HttpClient.newHttpClient();
            HttpResponse<String> response = http.send(request,BodyHandlers.ofString());
            System.out.println(response.body());           
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        */

        return command;
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
