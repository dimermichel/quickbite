package com.michelmaia.quickbite.infrastructure.security;

import com.michelmaia.quickbite.application.service.TokenGenerator;
import com.michelmaia.quickbite.domain.user.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Infrastructure Adapter: JWT Token Generator
 * Implements token generation using existing JWT infrastructure
 */
@Component
public class TokenGeneratorAdapter implements TokenGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenGeneratorAdapter.class);
    
    private final SecurityConfig securityConfig;
    
    public TokenGeneratorAdapter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }
    
    @Override
    public String generateToken(String username, List<Role> roles, Date issuedAt, Date expiration) {
        // DEBUG: Log roles before creating token
        LOGGER.info("Generating token for user: {} with roles: {}", username, roles);
        
        JWTObject jwtObject = new JWTObject();
        jwtObject.setSubject(username);
        jwtObject.setIssuedAt(issuedAt);
        jwtObject.setExpiration(expiration);
        
        // Convert domain roles to model roles (for JWT compatibility)
        jwtObject.setRoles(roles.stream()
            .toList());
        
        String token = JWTCreator.create(
            securityConfig.getPrefix(),
            securityConfig.getKey(),
            jwtObject
        );
        
        // DEBUG: Log the created token
        LOGGER.info("Token created successfully for user: {}", username);
        
        return token;
    }
    
    @Override
    public long getExpirationTime() {
        return securityConfig.getExpiration();
    }
}
