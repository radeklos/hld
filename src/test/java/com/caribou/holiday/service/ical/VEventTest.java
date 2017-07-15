package com.caribou.holiday.service.ical;

import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class VEventTest {

    @Test
    public void formatDateWithTimeZone() throws Exception {
        VEvent event = VEvent.builder()
                .dtStart(ZonedDateTime.of(2016, 6, 12, 12, 0, 0, 0, ZoneId.of("Europe/Prague")))
                .build();

        assertThat(event.toICal()).isEqualTo("BEGIN:VEVENT\n" +
                "DTSTART;TZID=Europe/Prague:20160612T120000\n" +
                "END:VEVENT");
    }
}
