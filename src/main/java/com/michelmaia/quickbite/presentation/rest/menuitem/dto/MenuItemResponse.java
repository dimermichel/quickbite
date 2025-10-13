package com.michelmaia.quickbite.presentation.rest.menuitem.dto;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;

import java.time.LocalDateTime;

/**
 * Response DTO for menu item operations
 */
public record MenuItemResponse(
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
    public static MenuItemResponse fromDomain(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl(),
                menuItem.isAvailable(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt()
        );
    }
}