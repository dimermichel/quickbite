package com.michelmaia.quickbite.dto;

import com.michelmaia.quickbite.model.Address;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
