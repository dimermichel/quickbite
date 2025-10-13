package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserNotFoundException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Update user profile
 */
public class UpdateUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UpdateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User execute(UpdateUserCommand command) {
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + command.userId()));
        
        // Update profile information
        Address newAddress = new Address(
            command.street(),
            command.city(),
            command.state(),
            command.zipCode()
        );
        
        user.updateProfile(command.name(), command.email(), newAddress);
        
        // Update password if provided
        if (command.password() != null && !command.password().isBlank()) {
            String encodedPassword = passwordEncoder.encode(command.password());
            user.changePassword(encodedPassword);
        }
        
        return userRepository.save(user);
    }
    
    public record UpdateUserCommand(
        Long userId,
        String name,
        String email,
        String password,
        String street,
        String city,
        String state,
        String zipCode
    ) {}
}
