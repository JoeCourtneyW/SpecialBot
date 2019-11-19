package com.joecourtneyw.specialbot.utils;

public class TimeoutException extends RuntimeException {
    private static final long serialVersionUID = -7857783526534112693L;

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(Throwable t) {
        super(t);
    }
}