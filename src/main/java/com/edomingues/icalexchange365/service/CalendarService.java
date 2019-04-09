package com.edomingues.icalexchange365.service;

import com.edomingues.icalexchange365.ical.IIcalFactory;
import com.edomingues.icalexchange365.model.Appointment;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IEventCollectionRequest;
import com.edomingues.icalexchange365.msgraph.GraphServiceClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CalendarService {

    @Autowired
    private GraphServiceClientManager graphServiceClientManager;

    @Autowired
    private IIcalFactory icalFactory;

    public String getCalendars(String userId, String startDateTime, String endDateTime) {

        IGraphServiceClient mGraphServiceClient = graphServiceClientManager.getGraphServiceClient(userId);

        List<Appointment> appointments = new ArrayList<>();

        List<Option> options = Arrays.asList(new QueryOption("startDateTime", startDateTime), new QueryOption("endDateTime", endDateTime));
        try {
            IEventCollectionRequest request = mGraphServiceClient.me().calendarView().buildRequest(options);
            IEventCollectionPage page;
            do {
                page = request.get();

                System.out.println("calendarView Success");

                for (Event event : page.getCurrentPage()) {
                    Appointment appointment = new Appointment();

                    appointment.subject = event.subject;
                    appointment.start = toDate(event.start);
                    appointment.end = toDate(event.end);
                    appointment.isAllDayEvent = event.isAllDay;
                    appointment.description = event.bodyPreview;
                    appointment.location = event.location.displayName;
                    appointments.add(appointment);
                }

                if(page.getNextPage()!=null) {
                    request = page.getNextPage().buildRequest();
                }
            } while(page.getNextPage() != null);

        } catch(ClientException ex) {
            System.out.println("calendarView failure " + ex);
        }

        try {
            return this.icalFactory.createCalendar(appointments);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Date toDate(DateTimeTimeZone dateTimeTimeZone) {
        var localDateTime = LocalDateTime.parse(dateTimeTimeZone.dateTime);
        var zonedDateTime = localDateTime.atZone(ZoneId.of(dateTimeTimeZone.timeZone));
        return Date.from(zonedDateTime.toInstant());
    }

}
