package com.Trimble.TrimbleCars.Exception;


public class LeaseLimitExceededException extends RuntimeException {
    public LeaseLimitExceededException(String message) {
        super(message);
    }
}
