
package com.michelmaia.quickbite.application.usecase.restaurant;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.exception.RestaurantNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;

/**
 * Use Case: Update restaurant information
 */
public class UpdateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    public UpdateRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant execute(UpdateRestaurantCommand command) {
        // Find existing restaurant
        Restaurant restaurant = restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(command.restaurantId()));

        // Update address if provided
        Address address = null;
        if (command.street() != null) {
            address = new Address(
                    command.street(),
                    command.city(),
                    command.state(),
                    command.zipCode()
            );
        }

        // Update restaurant info
        restaurant.updateInfo(
                command.name(),
                command.cuisine(),
                address,
                command.openingHours()
        );

        // Update rating if provided
        if (command.rating() != null) {
            restaurant.updateRating(command.rating());
        }

        // Update open status if provided
        if (command.isOpen() != null) {
            if (command.isOpen()) {
                restaurant.open();
            } else {
                restaurant.close();
            }
        }

        return restaurantRepository.save(restaurant);
    }

    public record UpdateRestaurantCommand(
            Long restaurantId,
            String name,
            String cuisine,
            String street,
            String city,
            String state,
            String zipCode,
            String openingHours,
            Double rating,
            Boolean isOpen
    ) {}
}