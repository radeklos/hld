package com.caribou.holiday.service;

import com.caribou.holiday.service.ical.VCalendar;
import com.caribou.holiday.service.ical.VEvent;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ICalServiceTest {

    @Test
    public void generateEmptyList() throws Exception {
        VCalendar generateCalendar = VCalendar.builder()
                .prodid("-//Google Inc//Google Calendar 70.9054//E")
                .calscale("GREGORIAN")
                .method("PUBLISH")
                .build();

        assertThat(generateCalendar.toICal()).isEqualTo("BEGIN:VCALENDAR\n" +
                "PRODID:-//Google Inc//Google Calendar 70.9054//E\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "METHOD:PUBLISH\n" +
                "END:VCALENDAR");
    }

    @Test
    public void icalWithEvents() throws Exception {
        VCalendar generateCalendar = VCalendar.builder()
                .prodid("-//Google Inc//Google Calendar 70.9054//E")
                .calscale("GREGORIAN")
                .method("PUBLISH")
                .vEvents(Collections.singletonList(
                        VEvent.builder()
                                .dtStartValueDate(LocalDate.of(2006, 12, 24))
                                .dtEndValueDate(LocalDate.of(2006, 12, 25))
                                .dtstamp(LocalDateTime.of(2017, 7, 11, 20, 7, 23, 0))
                                .uid("r0somb3deuokerbf6bj6vncft4@google.com")
                                .created(LocalDateTime.of(2006, 4, 13, 16, 59, 51, 0))
                                .description("")
                                .lastModified(LocalDateTime.of(2015, 7, 6, 16, 39, 39, 0))
                                .location("")
                                .status(VEvent.Status.CONFIRMED)
                                .summary("Štědrý den")
                                .build()
                ))
                .build();

        assertThat(generateCalendar.toICal()).isEqualTo("BEGIN:VCALENDAR\n" +
                "PRODID:-//Google Inc//Google Calendar 70.9054//E\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "METHOD:PUBLISH\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART;VALUE=DATE:20061224\n" +
                "DTEND;VALUE=DATE:20061225\n" +
                "DTSTAMP:20170711T200723Z\n" +
                "UID:r0somb3deuokerbf6bj6vncft4@google.com\n" +
                "DESCRIPTION:\n" +
                "CREATED:20060413T165951Z\n" +
                "LAST-MODIFIED:20150706T163939Z\n" +
                "LOCATION:\n" +
                "STATUS:CONFIRMED\n" +
                "SUMMARY:Štědrý den\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR");
    }

    @Test
    public void icalWithMultipleEvents() throws Exception {
        VEvent event = VEvent.builder()
                .dtStartValueDate(LocalDate.of(2006, 12, 24))
                .dtEndValueDate(LocalDate.of(2006, 12, 25))
                .dtstamp(LocalDateTime.of(2017, 7, 11, 20, 7, 23, 0))
                .uid("r0somb3deuokerbf6bj6vncft4@google.com")
                .created(LocalDateTime.of(2006, 4, 13, 16, 59, 51, 0))
                .description("")
                .lastModified(LocalDateTime.of(2015, 7, 6, 16, 39, 39, 0))
                .location("")
                .status(VEvent.Status.CONFIRMED)
                .summary("Štědrý den")
                .build();

        VCalendar generateCalendar = VCalendar.builder()
                .prodid("-//Google Inc//Google Calendar 70.9054//E")
                .calscale("GREGORIAN")
                .method("PUBLISH")
                .vEvents(Arrays.asList(event, event))
                .build();

        assertThat(generateCalendar.toICal()).isEqualTo("BEGIN:VCALENDAR\n" +
                "PRODID:-//Google Inc//Google Calendar 70.9054//E\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n" +
                "METHOD:PUBLISH\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART;VALUE=DATE:20061224\n" +
                "DTEND;VALUE=DATE:20061225\n" +
                "DTSTAMP:20170711T200723Z\n" +
                "UID:r0somb3deuokerbf6bj6vncft4@google.com\n" +
                "DESCRIPTION:\n" +
                "CREATED:20060413T165951Z\n" +
                "LAST-MODIFIED:20150706T163939Z\n" +
                "LOCATION:\n" +
                "STATUS:CONFIRMED\n" +
                "SUMMARY:Štědrý den\n" +
                "END:VEVENT\n" +
                "BEGIN:VEVENT\n" +
                "DTSTART;VALUE=DATE:20061224\n" +
                "DTEND;VALUE=DATE:20061225\n" +
                "DTSTAMP:20170711T200723Z\n" +
                "UID:r0somb3deuokerbf6bj6vncft4@google.com\n" +
                "DESCRIPTION:\n" +
                "CREATED:20060413T165951Z\n" +
                "LAST-MODIFIED:20150706T163939Z\n" +
                "LOCATION:\n" +
                "STATUS:CONFIRMED\n" +
                "SUMMARY:Štědrý den\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR");
    }

}
