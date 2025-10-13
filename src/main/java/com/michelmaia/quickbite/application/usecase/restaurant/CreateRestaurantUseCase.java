
package com.michelmaia.quickbite.application.usecase.restaurant;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.exception.UnauthorizedRestaurantOwnerException;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;
import com.michelmaia.quickbite.domain.user.entity.Role;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserNotFoundException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

/**
 * Use Case: Create a new restaurant
 */
public class CreateRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public CreateRestaurantUseCase(RestaurantRepository restaurantRepository,
                                   UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public Restaurant execute(CreateRestaurantCommand command) {
        // Business rule: Owner must exist
        User owner = userRepository.findById(command.ownerId())
                .orElseThrow(() -> new UserNotFoundException("Owner not found with id: " + command.ownerId()));

        // Business rule: Owner must have OWNER or ADMIN role
        if (!owner.hasRole(Role.OWNER) && !owner.hasRole(Role.ADMIN)) {
            throw new UnauthorizedRestaurantOwnerException("User does not have permission to own a restaurant. OWNER or ADMIN role required.");
        }

        // Create address
        Address address = new Address(
                command.street(),
                command.city(),
                command.state(),
                command.zipCode()
        );

        // Create restaurant
        Restaurant restaurant = Restaurant.createNew(
                command.ownerId(),
                command.name(),
                command.cuisine(),
                address,
                command.openingHours()
        );

        // Optional: Set initial rating and open status if provided
        if (command.rating() != null) {
            restaurant.updateRating(command.rating());
        }
        if (command.isOpen() != null && !command.isOpen()) {
            restaurant.close();
        }

        return restaurantRepository.save(restaurant);
    }

    public record CreateRestaurantCommand(
            Long ownerId,
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