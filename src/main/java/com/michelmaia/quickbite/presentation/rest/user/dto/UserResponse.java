package com.michelmaia.quickbite.presentation.rest.user.dto;

import com.michelmaia.quickbite.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
    Long id,
    String name,
    String email,
    String username,
    AddressResponse address,
    List<String> roles,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserResponse fromDomain(User user) {
        AddressResponse addressResponse = user.getAddress() != null
            ? new AddressResponse(
                user.getAddress().getStreet(),
                user.getAddress().getCity(),
                user.getAddress().getState(),
                user.getAddress().getZipCode()
            )
            : null;
        
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getUsername(),
            addressResponse,
            user.getRoles().stream().map(role -> role.getName()).toList(),
            user.isEnabled(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    public record AddressResponse(
        String street,
        String city,
        String state,
        String zipCode
    ) {}
}
