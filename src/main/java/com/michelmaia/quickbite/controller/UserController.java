package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.dto.ErrorDTO;
import com.michelmaia.quickbite.model.User;
import com.michelmaia.quickbite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "User Management", description = "Endpoints for managing users")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "**OPEN ROUTE** - Registers a new user with default USER role. Admin role __cannot__ be set during registration.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody User user) {
        LOGGER.info("POST -> /api/users/register - Registering new user: {}", user.getUsername());
        // Set the default role as USER for registration
        userService.registerUser(user); // This should assign a USER role by default
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Find all users", description = "Fetches a paginated list of all users, optionally filtered by role ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
            })
    @GetMapping
    public ResponseEntity<List<User>> findAllUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "roleId", required = false) Long roleId) {
        LOGGER.info("GET -> /api/users - Fetching all users - Page: {}, Size: {}", page, size);
        List<User> users = userService.findAllUsers(page, size, roleId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Find user by ID", description = "Fetches a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findUserById(@Parameter(description = "User Id") @PathVariable Long id) {
        LOGGER.info("GET -> /api/users/id - Fetching user with ID: {}", id);
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        } else {
            LOGGER.warn("GET -> /api/users/id - User with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Save a new user", description = "Saves a new user to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping
    public ResponseEntity<?> saveUser(@Valid @RequestBody User user) {
        LOGGER.info("POST -> /api/users - Saving user: {}", user);
        try {
            userService.saveUser(user);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException e) {
            LOGGER.warn("POST -> /api/users - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("POST -> /api/users - Data integrity violation: {}", e.getMessage());
            String message = "Email already exists. Please use a different email address.";
            if (e.getMessage().contains("users_username_key")) {
                message = "Username already exists. Please use a different username.";
            }
            return ResponseEntity.badRequest().body(new ErrorDTO(message));
        }
    }


    @Operation(summary = "Update user by ID", description = "Updates a user's information by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.updateUser(user, id);
            LOGGER.info("PUT -> /api/users/id - Updating user with ID: {} - User: {}", id, user);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.error("PUT -> /api/users/id - Error updating user with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a user by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@Parameter(description = "User Id") @PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            LOGGER.info("DELETE -> /users/id - Deleting user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.error("DELETE -> /users/id - Error deleting user with ID: {} - {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}