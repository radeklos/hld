package com.caribou.holiday.service.ical;

import lombok.Builder;

import java.util.List;


@Builder
@ICalRoot("VCALENDAR")
public class VCalendar extends ICal {

    @ICalField("VERSION") //  2.0
    protected final String version = "2.0";
    @ICalField("PRODID") //  -//Google Inc//Google Calendar 70.9054//EN
    protected String prodid;
    @ICalField("CALSCALE") //  GREGORIAN
    protected String calscale;

    @ICalField("METHOD") //  PUBLISH
    protected String method;

    @ICalField("X-WR-CALNAME") //  České svátky
    protected String XWrCalName;

    @ICalField("X-WR-TIMEZONE") //  America/Los_Angeles
    protected String XWrTimeZone;

    @ICalField("X-WR-CALDESC") //  Czech Holidays / Státní svátky České republiky
    protected String XWrCalDesc;

    @ICalNested
    protected List<VEvent> vEvents;

}
