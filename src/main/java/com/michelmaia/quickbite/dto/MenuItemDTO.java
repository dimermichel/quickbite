package com.michelmaia.quickbite.dto;

import java.time.LocalDateTime;

public record MenuItemDTO(
        Long id,
        Long restaurantId,
        String name,
        String description,
        Double price,
        String imageUrl,
        Boolean available,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
