package com.barbup.barbup_api.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Invalid or expired verification code");
    }
}
