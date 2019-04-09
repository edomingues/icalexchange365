package com.edomingues.icalexchange365.web.rest;

import com.edomingues.icalexchange365.service.AuthenticationService;
import com.edomingues.icalexchange365.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

@RestController
public class CalendarRestController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CalendarService calendarService;

    @Value("${default.start.date.previous.days}")
    private long DEFAULT_START_DATE_PREVIOUS_DAYS;

    @Value("${default.end.date.following.days}")
    private long DEFAULT_END_DATE_FOLLOWING_DAYS;

    @GetMapping("authorize")
    public ResponseEntity authorize(@RequestParam(required = false) String code, @RequestParam(defaultValue = "") String userId, @RequestParam(defaultValue = "") String state) throws URISyntaxException, InterruptedException, ExecutionException, IOException {

        if (StringUtils.isEmpty(code)) {
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).header(HttpHeaders.LOCATION, authenticationService.getAuthorizationUrl(userId)).build();
        } else {
            authenticationService.getAccessTokenFromCodeAndState(code, state);
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(value = "calendar", produces = "text/calendar; charset=UTF-8")
    public ResponseEntity<String> calendar(@RequestParam(required = false) String startDateTime, @RequestParam(required = false) String endDateTime, @RequestParam(defaultValue = "") String userId) throws InterruptedException, ExecutionException, IOException {

        if (startDateTime == null) {
            startDateTime = LocalDateTime.now().minus(DEFAULT_START_DATE_PREVIOUS_DAYS, ChronoUnit.DAYS).toString();
        }
        if (endDateTime == null) {
            endDateTime = LocalDateTime.now().plus(DEFAULT_END_DATE_FOLLOWING_DAYS, ChronoUnit.DAYS).toString();
        }

        System.out.println("startDateTime="+startDateTime);
        System.out.println("endDateTime="+endDateTime);

        if(!authenticationService.isAuthorized(userId)) {
            System.err.println("not authorized");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(calendarService.getCalendars(userId, startDateTime, endDateTime));

    }

    @GetMapping
    public void logout(@RequestParam(defaultValue = "") String userId) {
        this.authenticationService.deleteAccessToken(userId);
    }

}
