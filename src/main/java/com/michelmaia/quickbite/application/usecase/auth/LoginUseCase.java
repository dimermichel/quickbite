package com.michelmaia.quickbite.application.usecase.auth;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.application.service.TokenGenerator;
import com.michelmaia.quickbite.domain.auth.exception.AccountDisabledException;
import com.michelmaia.quickbite.domain.auth.exception.InvalidCredentialsException;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Use Case: User Login
 * Authenticates user and generates JWT token
 */
public class LoginUseCase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUseCase.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    
    public LoginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }
    
    public LoginResult execute(LoginCommand command) {
        // Find user by username
        User user = userRepository.findByUsername(command.username())
            .orElseThrow(InvalidCredentialsException::new);
        
        // DEBUG: Log roles
        LOGGER.info("User {} has roles: {}", user.getUsername(), user.getRoles());
        
        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }
        
        // Generate token
        long currentTime = System.currentTimeMillis();
        Date issuedAt = new Date(currentTime);
        Date expiration = new Date(currentTime + tokenGenerator.getExpirationTime());
        
        String token = tokenGenerator.generateToken(
            user.getUsername(),
            user.getRoles(),
            issuedAt,
            expiration
        );
        
        return new LoginResult(token, user.getUsername());
    }
    
    // Command and Result records
    public record LoginCommand(String username, String password) {}
    
    public record LoginResult(String token, String username) {}
}
