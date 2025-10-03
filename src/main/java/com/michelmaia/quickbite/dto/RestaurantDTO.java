package com.michelmaia.quickbite.dto;

import com.michelmaia.quickbite.model.Address;

import java.time.LocalDateTime;

public record RestaurantDTO(
        Long id,
        Long ownerId,
        Address address,
        String name,
        String cuisine,
        Double rating,
        String openingHours,
        Boolean isOpen,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
