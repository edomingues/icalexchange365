package com.edomingues.icalexchange365.model;

import java.io.Serializable;

public class AuthorizationState implements Serializable {

    private static final long serialVersionUID = 1L;

    public final String state;
    public final String userId;

    public AuthorizationState(String userId, String state) {
        this.userId = userId;
        this.state = state;
    }
}
