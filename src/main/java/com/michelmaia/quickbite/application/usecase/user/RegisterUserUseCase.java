package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserAlreadyExistsException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Register a new user
 * Single Responsibility: Handle user registration business logic
 */
public class RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User execute(RegisterUserCommand command) {
        // Business rule: Username must be unique
        if (userRepository.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + command.username());
        }
        
        // Business rule: Email must be unique
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + command.email());
        }
        
        // Create domain entity
        Address address = new Address(
            command.street(),
            command.city(),
            command.state(),
            command.zipCode()
        );
        
        User user = User.createNew(
            command.name(),
            command.email(),
            command.username(),
            command.password(),
            address
        );
        
        // Encode password before saving (infrastructure concern)
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        User userWithEncodedPassword = User.reconstruct(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getUsername(),
            encodedPassword,
            user.getAddress(),
            user.getRoles(),
            user.isEnabled(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
        
        // Persist
        return userRepository.save(userWithEncodedPassword);
    }
    
    // Command object (input data)
    public record RegisterUserCommand(
        String name,
        String email,
        String username,
        String password,
        String street,
        String city,
        String state,
        String zipCode
    ) {}
}
