/*
 * Copyright 2017 - 2025 Anton Tananaev (anton@traccar.org)
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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.traccar.BaseProtocolEncoder;
import org.traccar.Protocol;
import org.traccar.config.Keys;
import org.traccar.helper.DataConverter;
import org.traccar.helper.model.AttributeUtil;
import org.traccar.model.Command;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class HuabaoProtocolEncoder extends BaseProtocolEncoder {

    public HuabaoProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {

        boolean alternative = AttributeUtil.lookup(
                getCacheManager(), Keys.PROTOCOL_ALTERNATIVE.withPrefix(getProtocolName()), command.getDeviceId());

        ByteBuf id = Unpooled.wrappedBuffer(
                DataConverter.parseHex(getUniqueId(command.getDeviceId())));
        try {
            ByteBuf data = Unpooled.buffer();
            byte[] time = DataConverter.parseHex(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));

            switch (command.getType()) {
                case Command.TYPE_CUSTOM:
                    String model = getDeviceModel(command.getDeviceId());
                    if (model != null && Set.of("AL300", "GL100", "VL300").contains(model)) {
                        data.writeByte(1); // number of parameters
                        data.writeInt(0xF030); // AT command transparent transmission
                        int length = command.getString(Command.KEY_DATA).length();
                        data.writeByte(length);
                        data.writeCharSequence(command.getString(Command.KEY_DATA), StandardCharsets.US_ASCII);
                        return HuabaoProtocolDecoder.formatMessage(
                                0x7e, HuabaoProtocolDecoder.MSG_CONFIGURATION_PARAMETERS, id, false, data);
                    } else if ("BSJ".equals(model)) {
                        data.writeByte(1); // flag
                        var charset = Charset.isSupported("GBK") ? Charset.forName("GBK") : StandardCharsets.US_ASCII;
                        data.writeCharSequence(command.getString(Command.KEY_DATA), charset);
                        return HuabaoProtocolDecoder.formatMessage(
                                0x7e, HuabaoProtocolDecoder.MSG_SEND_TEXT_MESSAGE, id, false, data);
                    } else {
                        return Unpooled.wrappedBuffer(DataConverter.parseHex(command.getString(Command.KEY_DATA)));
                    }
                case Command.TYPE_REBOOT_DEVICE:
                    // 7e 81 05 00 01 xx xx xx xx xx xx 00 a1 04 4C 7e
                    //data.writeByte(1); // number of parameters
                    //data.writeByte(0x00);
                    //data.writeByte(0x23); // parameter id
                    //data.writeByte(1); // parameter value length
                    //data.writeByte(0x03); // restart
                    data.writeByte(0x00);
                    data.writeByte(0xa1);
                    data.writeByte(0x04);
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, data, true);
                case Command.TYPE_POSITION_PERIODIC_ORIG:
                    data.writeByte(1); // number of parameters
                    data.writeByte(0x06); // parameter id
                    data.writeByte(4); // parameter value length
                    data.writeInt(command.getInteger(Command.KEY_FREQUENCY));
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING_ORIG, id, false, data);
                case Command.TYPE_POSITION_PERIODIC:
                    data.writeByte(0x00);
                    data.writeByte(0xa9);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x22);
                    data.writeByte(4); // parameter value length
                    data.writeInt(command.getInteger(Command.KEY_FREQUENCY));
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                case Command.TYPE_POSITION_PERIODIC_STATIC:
                    data.writeByte(0x00);
                    data.writeByte(0xa9);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x27);
                    data.writeByte(4); // parameter value length
                    data.writeInt(command.getInteger(Command.KEY_FREQUENCY));
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                case Command.TYPE_TRANSPARENT:
                    data.writeByte(0x00);
                    data.writeByte(0x35);
                    data.writeByte(0xff);
                    var charsettr = Charset.isSupported("GBK") ? Charset.forName("GBK") : StandardCharsets.US_ASCII;
                        data.writeCharSequence(command.getString(Command.KEY_DATA), charsettr);
                    return HuabaoProtocolDecoder.formatMessage(
                        0x7e, HuabaoProtocolDecoder.MSG_TRANSPARENT, id, data, true);
                case Command.TYPE_TRANSPARENT_SER:
                    data.writeByte(0x00);
                    data.writeByte(0x35);
                    data.writeByte(0x41);
                    var charsettrser = Charset.isSupported("GBK") ? Charset.forName("GBK") : StandardCharsets.US_ASCII;
                        data.writeCharSequence(command.getString(Command.KEY_DATA), charsettrser);
                    return HuabaoProtocolDecoder.formatMessage(
                        0x7e, HuabaoProtocolDecoder.MSG_TRANSPARENT, id, data, true);
                case Command.TYPE_SET_APN:
                    data.writeByte(0x00);
                    data.writeByte(0xa9);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x10);
                    // data.writeByte(2); // parameter value length
                    data.writeByte(command.getString(Command.KEY_DATA).length()); // parameter value length
                    var charset = Charset.isSupported("GBK") ? Charset.forName("GBK") : StandardCharsets.US_ASCII;
                    data.writeCharSequence(command.getString(Command.KEY_DATA), charset);
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                case Command.TYPE_ALARM_ARM, Command.TYPE_ALARM_DISARM:
                    data.writeByte(1); // number of parameters
                    data.writeByte(0x24); // parameter id
                    String username = "user";
                    data.writeByte(1 + username.length()); // parameter value length
                    data.writeByte(command.getType().equals(Command.TYPE_ALARM_ARM) ? 0x01 : 0x00);
                    data.writeCharSequence(username, StandardCharsets.US_ASCII);
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, false, data);
                case Command.TYPE_ENGINE_STOP, Command.TYPE_ENGINE_RESUME:
                    if (alternative) {
                        data.writeByte(command.getType().equals(Command.TYPE_ENGINE_STOP) ? 0x01 : 0x00);
                        data.writeBytes(time);
                        return HuabaoProtocolDecoder.formatMessage(
                                0x7e, HuabaoProtocolDecoder.MSG_OIL_CONTROL, id, false, data);
                    } else {
                        if ("VL300".equals(getDeviceModel(command.getDeviceId()))) {
                            data.writeCharSequence(command.getType().equals(Command.TYPE_ENGINE_STOP) ? "#0;1" : "#0;0",
                                    StandardCharsets.US_ASCII);
                        } else {
                            data.writeByte(command.getType().equals(Command.TYPE_ENGINE_STOP) ? 0xf0 : 0xf1);
                        }
                        return HuabaoProtocolDecoder.formatMessage(
                                0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, false, data);
                    }
                case Command.TYPE_LIGHT_ON, Command.TYPE_LIGHT_OFF:
                    data.writeByte(0x00);
                    data.writeByte(0xa1); // parameter id
                    data.writeByte(0x21); // parameter id
                    data.writeByte(command.getType().equals(Command.TYPE_LIGHT_ON) ? 0x01 : 0x00);
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, data, true);
                case Command.TYPE_LIGHT_DURATION:
                    data.writeByte(0x00);
                    data.writeByte(0xa1); // parameter id
                    data.writeByte(0x23); // parameter id
                    //data.writeByte(0x04); // parameter value length
                    data.writeShort(command.getInteger(Command.KEY_DURATION));
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, data, true);
                case Command.TYPE_BUZZER_ON, Command.TYPE_BUZZER_OFF:
                    data.writeByte(0x00);
                    data.writeByte(0xa1); // parameter id
                    data.writeByte(0x22); // parameter id
                    data.writeByte(command.getType().equals(Command.TYPE_BUZZER_ON) ? 0x01 : 0x00);
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, data, true);
                case Command.TYPE_BUZZER_DURATION:
                    data.writeByte(0x00);
                    data.writeByte(0xa1); // parameter id
                    data.writeByte(0x24); // parameter id
                    //data.writeByte(0x04); // parameter value length
                    data.writeShort(command.getInteger(Command.KEY_DURATION));
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_TERMINAL_CONTROL, id, data, true);
                case Command.TYPE_CORNER_RADIUS:
                    data.writeByte(0x00);
                    data.writeByte(0xa7);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x30);
                    data.writeByte(4); // parameter value length
                    data.writeShort(command.getInteger(Command.KEY_RADIUS));
                    return HuabaoProtocolDecoder.formatMessage(
                        0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                case Command.TYPE_LIVEMODE_ON:
                    data.writeByte(0x00);
                    data.writeByte(0xa9);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x22);
                    data.writeByte(4); // parameter value length
                    data.writeInt(2); // 2s interval
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                case Command.TYPE_LIVEMODE_OFF:
                    data.writeByte(0x00);
                    data.writeByte(0xa9);
                    data.writeByte(1);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x00);
                    data.writeByte(0x22);
                    data.writeByte(4); // parameter value length
                    data.writeInt(60); // 60s interval
                    return HuabaoProtocolDecoder.formatMessage(
                            0x7e, HuabaoProtocolDecoder.MSG_PARAMETER_SETTING, id, data, true);
                default:
                    return null;
            }
        } finally {
            id.release();
        }
    }
}
