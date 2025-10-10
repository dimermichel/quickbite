package com.michelmaia.quickbite.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MenuItemDTO(
        Long id,
        Long restaurantId,
        String name,
        String description,
        Double price,
        String imageUrl,
        Boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
