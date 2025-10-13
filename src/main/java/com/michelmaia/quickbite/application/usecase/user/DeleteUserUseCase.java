package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserNotFoundException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Delete a user
 * Business Rule: User can only be deleted if they don't own any restaurants
 * 
 * Note: In a full Clean Architecture, we would inject a RestaurantRepository port
 * to check ownership. For now, the check is done at the infrastructure level.
 */
public class DeleteUserUseCase {
    
    private final UserRepository userRepository;
    
    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Execute the delete user use case
     * 
     * @param userId The ID of the user to delete
     * @throws UserNotFoundException if user doesn't exist
     * @throws UserHasRestaurantsException if user owns restaurants (thrown by infrastructure)
     */
    public void execute(Long userId) {
        // Find the user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        // Business rule: Check if user owns restaurants
        // This check is currently done at the repository/infrastructure level
        // because it involves cross-aggregate validation (User -> Restaurant)
        // In a more complete implementation, we would:
        // 1. Inject a RestaurantQueryPort
        // 2. Check: if (restaurantQuery.countByOwnerId(userId) > 0) throw exception
        // 3. Or use a domain event/saga pattern for complex cross-aggregate operations
        
        // Delete the user
        // The repository implementation will check for restaurant ownership
        // and throw UserHasRestaurantsException if needed
        userRepository.delete(user);
    }
}
