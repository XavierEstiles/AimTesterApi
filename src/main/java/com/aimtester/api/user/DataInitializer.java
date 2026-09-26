package com.aimtester.api.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea la cuenta {@code admin} de desarrollo solo si la tabla está vacía.
 * Se puede desactivar con la propiedad {@code aimtester.seed-admin=false}.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedAdmin;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${aimtester.seed-admin:true}") boolean seedAdmin) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedAdmin = seedAdmin;
    }

    @Override
    public void run(String... args) {
        if (!seedAdmin || userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User(
                "admin",
                "admin@aimtester.dev",
                passwordEncoder.encode("admin123"),
                UserRole.ADMIN
        ));

        log.warn("Se ha creado la cuenta de desarrollo 'admin'. Si no la necesitas, desactívala con aimtester.seed-admin=false.");
    }
}
