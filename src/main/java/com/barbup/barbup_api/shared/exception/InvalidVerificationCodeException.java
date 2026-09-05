package com.barbup.barbup_api.shared.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Invalid or expired verification code");
    }
}
