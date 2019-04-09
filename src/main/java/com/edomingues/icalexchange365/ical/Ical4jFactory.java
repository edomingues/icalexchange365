package com.edomingues.icalexchange365.ical;

import com.edomingues.icalexchange365.model.Appointment;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

@Component
public class Ical4jFactory implements IIcalFactory {

    private final UidGenerator uidGenerator;
    private final boolean SUPPORT_ALL_DAY_EVENTS;
    private final TimeZone timeZone;

    public Ical4jFactory(@Value("${support.all.day.events}") boolean supportAllDayEvents, @Value("${time.zone}") String timeZone) {
        this.uidGenerator = new RandomUidGenerator();
        this.SUPPORT_ALL_DAY_EVENTS = supportAllDayEvents;
        this.timeZone = TimeZone.getTimeZone(timeZone);
    }

    public String createCalendar(List<Appointment> appointments) throws IOException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Appointment appointment : appointments)
            calendar.getComponents().add(createVEvent(appointment));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.setValidating(true);
        outputter.output(calendar, baos);

        return baos.toString();
    }

    private VEvent createVEvent(Appointment appointment) {
        VEvent event;

        if (appointment.isAllDayEvent && SUPPORT_ALL_DAY_EVENTS) {
            event = new VEvent(date(appointment.start), date(appointment.end), appointment.subject);
        } else {
            DateTime start = new DateTime(appointment.start);
            start.setUtc(true);
            DateTime end = new DateTime(appointment.end);
            end.setUtc(true);

            event = new VEvent(start, end, appointment.subject);
        }

        event.getProperties().add(new Description(appointment.description));
        event.getProperties().add(new Location(appointment.location));

        event.getProperties().add(uidGenerator.generateUid());

        return event;
    }

    /**
     * Converts the given date to the configured time zone and returns the day, month and year.
     * This fixes the problem of all day events with no UTC time zone.
     * For instance, if we have an all day event starting in 2013-12-25 00:00 GMT+1,
     * then the EWS returns 2013-12-24 23:00 UTC which gives day 24 when what we want is day 25.
     *
     * @param date date/time to convert
     * @return date (day, month, year) in desired time zone
     */
    private Date date(java.util.Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance(this.timeZone);

        calendar.setTime(date);
        java.util.Calendar dayCalendar = java.util.Calendar.getInstance();
        dayCalendar.set(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
        return new Date(dayCalendar.getTime());
    }
}
