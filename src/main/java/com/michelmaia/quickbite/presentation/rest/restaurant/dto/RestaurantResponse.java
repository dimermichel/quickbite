package com.michelmaia.quickbite.presentation.rest.restaurant.dto;

import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;

import java.time.LocalDateTime;

/**
 * Response DTO for restaurant operations
 */
public record RestaurantResponse(
        Long id,
        Long ownerId,
        String name,
        String cuisine,
        AddressResponse address,
        String openingHours,
        Double rating,
        Boolean isOpen,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RestaurantResponse fromDomain(Restaurant restaurant) {
        AddressResponse addressResponse = restaurant.getAddress() != null
                ? new AddressResponse(
                restaurant.getAddress().getStreet(),
                restaurant.getAddress().getCity(),
                restaurant.getAddress().getState(),
                restaurant.getAddress().getZipCode()
        )
                : null;

        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getOwnerId(),
                restaurant.getName(),
                restaurant.getCuisine(),
                addressResponse,
                restaurant.getOpeningHours(),
                restaurant.getRating(),
                restaurant.isOpen(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }

    public record AddressResponse(
            String street,
            String city,
            String state,
            String zipCode
    ) {}
}