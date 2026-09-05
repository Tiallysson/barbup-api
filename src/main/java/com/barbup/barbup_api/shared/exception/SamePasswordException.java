package com.barbup.barbup_api.shared.exception;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("The new password cannot be the same as the current password.");
    }
}
