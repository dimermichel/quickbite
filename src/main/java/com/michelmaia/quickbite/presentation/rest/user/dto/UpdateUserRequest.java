package com.michelmaia.quickbite.presentation.rest.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a user
 */
public record UpdateUserRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    // Password is optional on update
    @Size(min = 4, message = "Password must be at least 4 characters if provided")
    String password,
    
    @Valid
    AddressRequest address
) {
    public record AddressRequest(
        @NotBlank(message = "Street is required") String street,
        @NotBlank(message = "City is required") String city,
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "Zip code is required") String zipCode
    ) {}
}
