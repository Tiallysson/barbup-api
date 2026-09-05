package com.barbup.barbup_api.shared.exception;

public class EmailAlreadyVerifiedException extends RuntimeException {
    public EmailAlreadyVerifiedException(String email) {
        super("Email address '" + email + "' is already verified");
    }
}
