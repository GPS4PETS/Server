package org.traccar.protocol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class H02ProtocolEncoderTest extends ProtocolTest {

    private H02ProtocolEncoder encoder;
    private final Date time = Date.from(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 2, 3)).atZone(ZoneOffset.systemDefault()).toInstant());

    @BeforeEach
    public void before() throws Exception {
        encoder = inject(new H02ProtocolEncoder(null));
    }

}
