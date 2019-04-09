package com.edomingues.icalexchange365.model;

import java.io.Serializable;
import java.util.Date;

public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Date expirationTime;

    public final String userId;
    public final String accessToken;
    public final String refreshToken;

    public AccessToken(String userId, String accessToken, Date expirationTime, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expirationTime = expirationTime;
        this.refreshToken = refreshToken;
    }

    public boolean isExpired() {
        return this.expirationTime.before(new Date());
    }
}
