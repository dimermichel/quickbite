
package com.michelmaia.quickbite.presentation.rest.menuitem.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating a menu item
 */
public record CreateMenuItemRequest(
        @NotNull(message = "Restaurant ID is required")
        Long restaurantId,

        @NotBlank(message = "Menu item name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
        @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
        Double price,

        String imageUrl,

        Boolean isAvailable
) {}