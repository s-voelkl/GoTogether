package com.gotogether.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Brief overview: Application security configuration.
 *
 * Summary: This class configures the security settings for the backend
 * application,
 * including defining the password encoder and setting HTTP request
 * authorization rules.
 */
@Configuration
public class SecurityConfig {

    /**
     * Brief overview: Configures the security filter chain.
     *
     * Summary: Sets up the HTTP security rules, primarily disabling CSRF and
     * allowing all HTTP requests
     * to proceed without authentication.
     *
     * @param http The HttpSecurity object used to construct the security rules.
     * @return The configured SecurityFilterChain instance.
     * @throws Exception If an error occurs while building the security
     *                   configuration.
     *
     *                   Note: Currently configured to permit all requests. This
     *                   should be secured prior to production deployment.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }

    /**
     * Brief overview: Provides the password encoder bean.
     *
     * Summary: Creates and returns a BCrypt password encoder, which is used for
     * securely hashing
     * user and company passwords.
     *
     * @return A PasswordEncoder instance utilizing the BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}