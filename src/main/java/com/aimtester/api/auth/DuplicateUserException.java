package com.aimtester.api.auth;

/** El nombre de usuario o el correo ya están registrados. */
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }
}
