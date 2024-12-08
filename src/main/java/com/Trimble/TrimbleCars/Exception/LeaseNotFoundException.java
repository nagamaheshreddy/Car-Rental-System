package com.Trimble.TrimbleCars.Exception;


public class LeaseNotFoundException extends RuntimeException {
    public LeaseNotFoundException(String message) {
        super(message);
    }
}
