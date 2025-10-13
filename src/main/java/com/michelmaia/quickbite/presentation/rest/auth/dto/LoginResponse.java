package com.michelmaia.quickbite.presentation.rest.auth.dto;

/**
 * Response DTO for successful login
 */
public record LoginResponse(
    String token,
    String username
) {}
