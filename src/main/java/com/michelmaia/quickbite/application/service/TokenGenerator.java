package com.michelmaia.quickbite.application.service;

import com.michelmaia.quickbite.domain.user.entity.Role;

import java.util.Date;
import java.util.List;

/**
 * Application Service: Token generation
 * Defines the contract for JWT token generation
 */
public interface TokenGenerator {
    
    /**
     * Generate a JWT token for the user
     * 
     * @param username The username
     * @param roles The user's roles
     * @param issuedAt Token issued date
     * @param expiration Token expiration date
     * @return The generated JWT token
     */
    String generateToken(String username, List<Role> roles, Date issuedAt, Date expiration);
    
    /**
     * Get token expiration time in milliseconds
     */
    long getExpirationTime();
}
