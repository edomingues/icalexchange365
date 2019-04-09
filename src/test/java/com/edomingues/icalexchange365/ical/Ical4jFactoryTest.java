package com.edomingues.icalexchange365.ical;

import com.edomingues.icalexchange365.model.Appointment;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

public class Ical4jFactoryTest {

    @Test
    public void testCreateCalendar() throws Exception {
        IIcalFactory icalFactory=new Ical4jFactory(true, "Europe/Lisbon");

        Appointment appointment=new Appointment();
        appointment.subject="test subject";
        appointment.isAllDayEvent=true;
        appointment.start= Date.valueOf(LocalDate.parse("2019-05-01"));
        appointment.end= Date.valueOf(LocalDate.parse("2019-05-01"));

        String ical=icalFactory.createCalendar(Arrays.asList(appointment));
        System.out.println(ical);

    }
}