package com.michelmaia.quickbite.dto;

import jakarta.validation.constraints.NotNull;

public record LoginDTO(
        @NotNull(message = "Username cannot be null")
        String username,
        @NotNull(message = "Password cannot be null")
        String password
) {}
