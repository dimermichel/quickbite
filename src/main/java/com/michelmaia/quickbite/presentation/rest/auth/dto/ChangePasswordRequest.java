package com.michelmaia.quickbite.presentation.rest.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for password change
 */
public record ChangePasswordRequest(
    @NotBlank(message = "Username is required")
    String username,
    
    @NotBlank(message = "Current password is required")
    String currentPassword,
    
    @NotBlank(message = "New password is required")
    @Size(min = 4, message = "New password must be at least 4 characters")
    String newPassword
) {}
