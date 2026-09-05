package com.barbup.barbup_api.shared.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("Token is invalid");
    }
}
