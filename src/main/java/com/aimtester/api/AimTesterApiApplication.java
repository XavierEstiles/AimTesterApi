package com.aimtester.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AimTesterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AimTesterApiApplication.class, args);
    }
}
