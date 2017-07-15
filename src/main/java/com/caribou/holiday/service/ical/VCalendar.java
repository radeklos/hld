package com.caribou.holiday.service.ical;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
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

    @ICalField("X-PUBLISHED-TTL")
    protected String xPublishedTTL;

    @ICalField("X-WR-CALNAME") //  České svátky
    protected String xWrCalName;

    @ICalField("X-WR-TIMEZONE") //  America/Los_Angeles
    protected String xWrTimeZone;

    @ICalField("X-WR-CALDESC") //  Czech Holidays / Státní svátky České republiky
    protected String xWrCalDesc;

    @ICalNested
    protected List<VEvent> vEvents;

}
