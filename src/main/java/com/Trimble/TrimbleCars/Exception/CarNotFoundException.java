package com.Trimble.TrimbleCars.Exception;


public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String message) {
        super(message);
    }
}


