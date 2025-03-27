package com.cpsc559.server.config;

import com.cpsc559.server.security.SimplePasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Class that defines a password encoder that can be used in the application.
 *
 * @see com.cpsc559.server.controller.AuthController for example usage.
 */
@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SimplePasswordHasher();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
