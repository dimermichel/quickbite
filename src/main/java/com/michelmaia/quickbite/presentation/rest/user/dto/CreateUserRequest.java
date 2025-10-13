package com.michelmaia.quickbite.presentation.rest.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating a user
 * Used for both registration and admin user creation
 */
public record CreateUserRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters")
    String password,
    
    @Valid
    AddressRequest address,
    
    // Optional fields for admin user creation
    List<Long> roleIds,
    Boolean enabled
) {
    public record AddressRequest(
        @NotBlank(message = "Street is required") String street,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "Zip code is required") String zipCode
    ) {}
}
