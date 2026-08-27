package com.barbup.barbup_api.exception;

import java.time.DayOfWeek;

public class BusinessHourConflictException extends RuntimeException {
    public BusinessHourConflictException(DayOfWeek dayOfWeek) {
        super("Business hours for '" + dayOfWeek + "' are already registered for this barbershop");
    }
}
