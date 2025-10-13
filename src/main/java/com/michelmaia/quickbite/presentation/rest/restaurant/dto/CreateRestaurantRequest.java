package com.michelmaia.quickbite.presentation.rest.restaurant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a restaurant
 */
public record CreateRestaurantRequest(
        @NotNull(message = "Owner ID is required")
        Long ownerId,

        @NotBlank(message = "Restaurant name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Cuisine type is required")
        String cuisine,

        @Valid
        @NotNull(message = "Address is required")
        AddressRequest address,

        String openingHours,

        @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
        @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
        Double rating,

        Boolean isOpen
) {
    public record AddressRequest(
            @NotBlank(message = "Street is required") String street,
            @NotBlank(message = "City is required") String city,
            @NotBlank(message = "State is required") String state,
            @NotBlank(message = "Zip code is required") String zipCode
    ) {}
}