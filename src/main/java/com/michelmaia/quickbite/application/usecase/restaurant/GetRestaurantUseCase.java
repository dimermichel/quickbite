package com.michelmaia.quickbite.application.usecase.restaurant;

import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.exception.RestaurantNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;

/**
 * Use Case: Get restaurant by ID
 */
public class GetRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    public GetRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant execute(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }
}