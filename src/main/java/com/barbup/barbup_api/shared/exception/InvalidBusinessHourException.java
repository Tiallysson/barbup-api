package com.barbup.barbup_api.shared.exception;

public class InvalidBusinessHourException extends RuntimeException {
    public InvalidBusinessHourException(String message) {
        super(message);
    }
}
