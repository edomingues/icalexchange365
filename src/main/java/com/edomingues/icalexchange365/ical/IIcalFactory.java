package com.edomingues.icalexchange365.ical;

import com.edomingues.icalexchange365.model.Appointment;

import java.io.IOException;
import java.util.List;


public interface IIcalFactory {
	
	public String createCalendar(List<Appointment> appointments) throws IOException;
}
