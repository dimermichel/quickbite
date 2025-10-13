package com.michelmaia.quickbite.presentation.rest.user;

import com.michelmaia.quickbite.application.dto.PageResponseDTO;
import com.michelmaia.quickbite.application.usecase.user.*;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.presentation.rest.user.dto.CreateUserRequest;
import com.michelmaia.quickbite.presentation.rest.user.dto.UpdateUserRequest;
import com.michelmaia.quickbite.presentation.rest.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer: REST Controller for User endpoints
 * This controller is thin - it delegates to use cases
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management endpoints")
public class UserController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    
    public UserController(
            RegisterUserUseCase registerUserUseCase,
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            ListUsersUseCase listUsersUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user (public endpoint)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        var command = new RegisterUserUseCase.RegisterUserCommand(
            request.name(),
            request.email(),
            request.username(),
            request.password(),
            request.address().street(),
            request.address().city(),
            request.address().state(),
            request.address().zipCode()
        );
        
        User user = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(UserResponse.fromDomain(user));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var command = new CreateUserUseCase.CreateUserCommand(
            request.name(),
            request.email(),
            request.username(),
            request.password(),
            request.address().street(),
            request.address().city(),
            request.address().state(),
            request.address().zipCode(),
            request.roleIds(),
            request.enabled()
        );
        
        User user = createUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(UserResponse.fromDomain(user));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "List all users with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Authentication required")
    })
    public ResponseEntity<PageResponseDTO<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long roleId) {
        
        var query = new ListUsersUseCase.ListUsersQuery(page, size, roleId);
        PageResponseDTO<User> usersPage = listUsersUseCase.execute(query);
        
        // Convert domain entities to DTOs
        List<UserResponse> userResponses = usersPage.getData().stream()
            .map(UserResponse::fromDomain)
            .toList();
        
        PageResponseDTO<UserResponse> response = new PageResponseDTO<>(
            userResponses,
            usersPage.getPage(),
            usersPage.getSize(),
            usersPage.getTotalElements()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Authentication required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User user = getUserUseCase.execute(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        var command = new UpdateUserUseCase.UpdateUserCommand(
            id,
            request.name(),
            request.email(),
            request.password(),
            request.address().street(),
            request.address().city(),
            request.address().state(),
            request.address().zipCode()
        );
        
        updateUserUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Cannot delete - user owns restaurants")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}