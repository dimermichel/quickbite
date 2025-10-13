package com.michelmaia.quickbite.application.usecase.restaurant;

import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.exception.RestaurantNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;

/**
 * Use Case: Delete a restaurant
 * Business Rule: Only the owner or admin can delete
 * (Authorization is handled at the presentation layer)
 */
public class DeleteRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    public DeleteRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void execute(Long restaurantId) {
        // Find restaurant
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        // Delete restaurant
        // Note: Menu items should be cascade deleted by database FK constraints
        restaurantRepository.delete(restaurant);
    }
}