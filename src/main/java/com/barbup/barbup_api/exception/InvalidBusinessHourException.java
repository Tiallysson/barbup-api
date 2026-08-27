package com.barbup.barbup_api.exception;

public class InvalidBusinessHourException extends RuntimeException {
    public InvalidBusinessHourException(String message) {
        super(message);
    }
}
