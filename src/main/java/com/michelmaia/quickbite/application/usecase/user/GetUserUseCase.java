package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserNotFoundException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Get user by ID
 */
public class GetUserUseCase {
    
    private final UserRepository userRepository;
    
    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User execute(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
}
