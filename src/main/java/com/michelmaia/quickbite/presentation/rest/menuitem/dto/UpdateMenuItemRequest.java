package com.michelmaia.quickbite.presentation.rest.menuitem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a menu item
 */
public record UpdateMenuItemRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        String description,

        @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
        @DecimalMax(value = "999999.99", message = "Price cannot exceed 999999.99")
        Double price,

        String imageUrl,

        Boolean isAvailable
) {}