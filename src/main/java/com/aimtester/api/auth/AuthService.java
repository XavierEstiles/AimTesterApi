package com.aimtester.api.auth;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimtester.api.security.JwtService;
import com.aimtester.api.user.User;
import com.aimtester.api.user.UserRepository;
import com.aimtester.api.user.UserRole;

@Service
public class AuthService {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 30;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_EMAIL_LENGTH = 254;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Autentica y devuelve el JWT. La fecha del último acceso queda registrada. */
    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        userRepository.findByUsername(authentication.getName()).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });

        return jwtService.generateToken(authentication.getName());
    }

    /** Crea la cuenta y devuelve el JWT para que el cliente quede conectado. */
    @Transactional
    public String register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Falta el cuerpo de la petición.");
        }

        String username = normalize(request.username(), "username");
        String email = normalize(request.email(), "email");
        String password = request.password() == null ? "" : request.password();

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "El nombre de usuario debe tener entre " + MIN_USERNAME_LENGTH + " y " + MAX_USERNAME_LENGTH + " caracteres.");
        }
        if (!email.contains("@") || email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateUserException("Ese nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateUserException("Ese correo electrónico ya está registrado.");
        }

        User user = userRepository.save(new User(
                username,
                email,
                passwordEncoder.encode(password),
                UserRole.USER
        ));

        user.setLastLoginAt(LocalDateTime.now());
        return jwtService.generateToken(user.getUsername());
    }

    private String normalize(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + field + " es obligatorio.");
        }
        return value.trim();
    }
}
