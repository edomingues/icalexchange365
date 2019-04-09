package com.edomingues.icalexchange365.exception;

public class NoAccessTokenException extends RuntimeException {

    public NoAccessTokenException() {
        this("");
    }

    public NoAccessTokenException(String message) {
        super(message);
    }
}
