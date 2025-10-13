package com.michelmaia.quickbite.infrastructure.security;
import com.michelmaia.quickbite.application.service.PasswordEncoder;
import org.springframework.stereotype.Component;
/**
 * Infrastructure Adapter: Implements password encoding using Spring Security
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoder {
    private final org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder;
    public PasswordEncoderAdapter(org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder) {
        this.springPasswordEncoder = springPasswordEncoder;
    }
    @Override
    public String encode(String rawPassword) {
        return springPasswordEncoder.encode(rawPassword);
    }
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return springPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}