package com.michelmaia.quickbite.application.usecase.auth;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.domain.auth.exception.InvalidCredentialsException;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserNotFoundException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Change Password
 * Verifies current password and updates to new password
 */
public class ChangePasswordUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public ChangePasswordUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void execute(ChangePasswordCommand command) {
        // Find user
        User user = userRepository.findByUsername(command.username())
            .orElseThrow(() -> new UserNotFoundException("User not found: " + command.username()));
        
        // Verify current password
        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Use domain method to change password (validates new password)
        user.changePassword(command.newPassword());
        
        // Encode new password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        
        // Reconstruct user with encoded password
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
        
        // Save updated user
        userRepository.save(userWithEncodedPassword);
    }
    
    // Command record
    public record ChangePasswordCommand(
        String username,
        String currentPassword,
        String newPassword
    ) {}
}
