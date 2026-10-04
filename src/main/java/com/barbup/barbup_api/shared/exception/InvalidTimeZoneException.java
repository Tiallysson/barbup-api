package com.barbup.barbup_api.shared.exception;

public class InvalidTimeZoneException extends RuntimeException {
    public InvalidTimeZoneException(String timeZone) {
        super("Invalid time zone '" + timeZone + "'");
    }
}
