package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.user.entity.Role;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserAlreadyExistsException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use Case: Create a new user (Admin operation)
 * Different from RegisterUserUseCase - this allows admins to create users with specific roles
 */
public class CreateUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User execute(CreateUserCommand command) {
        // Business rule: Username must be unique
        if (userRepository.existsByUsername(command.username())) {
            throw new UserAlreadyExistsException("Username already exists: " + command.username());
        }
        
        // Business rule: Email must be unique
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException("Email already exists: " + command.email());
        }
        
        // Create address
        Address address = new Address(
            command.street(),
            command.city(),
            command.state(),
            command.zipCode()
        );
        
        // Convert role IDs to Role enums
        List<Role> roles = command.roleIds().stream()
            .map(Role::fromId)
            .collect(Collectors.toList());
        
        // Validate that at least one role is provided
        if (roles.isEmpty()) {
            roles = List.of(Role.USER); // Default to USER role
        }
        
        // Create user with specified roles and enabled status
        User user = User.reconstruct(
            null, // New user, no ID yet
            command.name(),
            command.email(),
            command.username(),
            command.password(),
            address,
            roles,
            command.enabled() != null ? command.enabled() : true, // Default to enabled if not specified
            null,
            null
        );
        
        // Encode password before saving
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
    
    /**
     * Command object for creating a user (Admin operation)
     * Allows specifying roles and enabled status
     */
    public record CreateUserCommand(
        String name,
        String email,
        String username,
        String password,
        String street,
        String city,
        String state,
        String zipCode,
        List<Long> roleIds,
        Boolean enabled
    ) {}
}
