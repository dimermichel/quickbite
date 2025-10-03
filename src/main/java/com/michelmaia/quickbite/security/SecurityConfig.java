package com.michelmaia.quickbite.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    
    @Value("${security.config.prefix}")
    private String prefix;
    
    @Value("${security.config.key}")
    private String key;
    
    @Value("${security.config.expiration}")
    private Long expiration;

    // Getters for accessing the values
    public String getPrefix() {
        return prefix;
    }

    public String getKey() {
        return key;
    }

    public Long getExpiration() {
        return expiration;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}