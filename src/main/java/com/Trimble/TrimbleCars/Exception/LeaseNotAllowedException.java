package com.Trimble.TrimbleCars.Exception;


public class LeaseNotAllowedException extends RuntimeException {
    public LeaseNotAllowedException(String message) {
        super(message);
    }
}

