package com.caribou.holiday.service.ical;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Builder
@ICalRoot("VEVENT")
public class VEvent extends ICal {

    @ICalField("DTSTART;VALUE=DATE")  // 20061224
    protected LocalDate dtStartValueDate;

    @ICalField("DTEND;VALUE=DATE")  // 20061225
    protected LocalDate dtEndValueDate;

    @ICalField("DTSTAMP")  // 20170711T200723Z
    protected LocalDateTime dtstamp;

    @ICalField("UID")  // r0somb3deuokerbf6bj6vncft4@google.com
    protected String uid;

    @ICalField("DESCRIPTION")
    protected String description;

    @ICalField("CREATED")  // 20060413T165951Z
    protected LocalDateTime created;

    @ICalField("LAST-MODIFIED")  // 20150706T163939Z
    protected LocalDateTime lastModified;

    @ICalField("LOCATION")  //
    protected String location;

    @ICalField("STATUS")  // CONFIRMED
    protected Status status;

    @ICalField("SUMMARY")  // Štědrý den
    protected String summary;

    public enum Status {

        TENTATIVE,
        CONFIRMED,
        CANCELLED

    }
}
