package com.michelmaia.quickbite.application.service;

/**
 * Output Port: Password encoding
 * Defined by application, implemented by infrastructure
 */
public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
