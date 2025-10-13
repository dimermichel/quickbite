package com.michelmaia.quickbite.presentation.rest.auth;

import com.michelmaia.quickbite.application.usecase.auth.ChangePasswordUseCase;
import com.michelmaia.quickbite.application.usecase.auth.LoginUseCase;
import com.michelmaia.quickbite.presentation.rest.auth.dto.ChangePasswordRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Presentation Layer: Authentication Controller
 * Handles login and password management endpoints
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Authentication", description = "User authentication and password management")
public class AuthController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    
    private final LoginUseCase loginUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    
    public AuthController(
            LoginUseCase loginUseCase,
            ChangePasswordUseCase changePasswordUseCase) {
        this.loginUseCase = loginUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "**OPEN ROUTE** - Authenticate a user and return a JWT token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful login",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid username or password",
            content = @Content(
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = "{\"message\": \"Invalid username or password\"}"
                )
            )
        )
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LOGGER.info("POST -> /api/login - Attempting login for user: {}", request.username());
        
        var command = new LoginUseCase.LoginCommand(
            request.username(),
            request.password()
        );
        
        LoginUseCase.LoginResult result = loginUseCase.execute(command);
        
        LoginResponse response = new LoginResponse(
            result.token(),
            result.username()
        );
        
        LOGGER.info("POST -> /api/login - Successful login for user: {}", request.username());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/change-password")
    @Operation(
        summary = "Change Password",
        description = "**OPEN ROUTE** - Change the password for a user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password changed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid current password",
            content = @Content(
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = "{\"message\": \"Current password is incorrect\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                examples = @ExampleObject(
                    name = "User Not Found",
                    value = "{\"message\": \"User not found\"}"
                )
            )
        )
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        LOGGER.info("POST -> /api/change-password - Changing password for user: {}", request.username());
        
        var command = new ChangePasswordUseCase.ChangePasswordCommand(
            request.username(),
            request.currentPassword(),
            request.newPassword()
        );
        
        changePasswordUseCase.execute(command);
        
        LOGGER.info("POST -> /api/change-password - Password changed successfully for user: {}", request.username());
        return ResponseEntity.ok().build();
    }
}
