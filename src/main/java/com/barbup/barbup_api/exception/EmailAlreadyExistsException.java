package com.barbup.barbup_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String mail) {
        super("Email address '" + mail + "' already exists");
    }
}
