package com.aimtester.api.auth;

/**
 * Petición de creación de cuenta. La validación se hace en {@link AuthService}.
 */
public record RegisterRequest(String username, String email, String password) {
}
