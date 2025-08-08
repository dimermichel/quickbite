package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.dto.ChangePasswordDTO;
import com.michelmaia.quickbite.dto.ErrorDTO;
import com.michelmaia.quickbite.dto.LoginDTO;
import com.michelmaia.quickbite.dto.SessionDTO;
import com.michelmaia.quickbite.model.User;
import com.michelmaia.quickbite.repository.UserRepository;
import com.michelmaia.quickbite.security.JWTCreator;
import com.michelmaia.quickbite.security.JWTObject;
import com.michelmaia.quickbite.security.SecurityConfig;
import com.michelmaia.quickbite.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@Tag(name = "Login", description = "Endpoints for user login and password management")
@RestController
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private final PasswordEncoder passwordEncoder;
    private final SecurityConfig securityConfig;
    private final UserRepository userRepository;
    private final UserService userService;

    public LoginController(PasswordEncoder passwordEncoder, SecurityConfig securityConfig, UserRepository userRepository, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.securityConfig = securityConfig;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Operation(
            summary = "User Login",
            description = "**OPEN ROUTE** - Authenticate a user and return a JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful login",
                            headers = @Header(name = "Authorization", description = "Bearer token"),
                            content = @Content(
                                    schema = @Schema(implementation = SessionDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid username or password",
                            content = @Content(
                                    examples = @ExampleObject(name = "Invalid Credentials", value = "{\"message\": \"Invalid username or password\"}")
                            ))
            }
    )
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        LOGGER.info("POST -> /api/login - Attempting login for user: {}", loginDTO.username());
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.username());

        if (userOpt.isEmpty() || !passwordEncoder.matches(loginDTO.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorDTO("Invalid username or password"));
        }

        User user = userOpt.get();

        JWTObject jwtObject = new JWTObject();
        jwtObject.setSubject(user.getUsername());
        jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
        jwtObject.setExpiration(new Date(System.currentTimeMillis() + securityConfig.getExpiration()));
        jwtObject.setRoles(user.getRoles());

        SessionDTO sessionDTO = new SessionDTO(JWTCreator.create(
                securityConfig.getPrefix(),
                securityConfig.getKey(),
                jwtObject
        ), user.getUsername());

        return ResponseEntity.ok(sessionDTO);
    }

    @Operation(
            summary = "Change Password",
            description = "**OPEN ROUTE** - Change the password for a user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password changed successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ChangePasswordDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid username or password",
                            content = @Content(
                                    examples = @ExampleObject(name = "Invalid Credentials", value = "{\"message\": \"Invalid username or password\"}")
                            )
                    )
            }
    )
    @PostMapping("/api/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        LOGGER.info("POST -> /api/change-password - Changing password for user: {}", changePasswordDTO.username());
        Optional<User> userOpt = userRepository.findByUsername(changePasswordDTO.username());

        if (userOpt.isEmpty() || !passwordEncoder.matches(changePasswordDTO.password(), userOpt.get().getPassword())) {
            LOGGER.warn("POST -> /api/change-password - Invalid credentials for user: {}", changePasswordDTO.username());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorDTO("Invalid username or password"));
        }

        User user = userOpt.get();
        userService.updateUserPassword(user, changePasswordDTO.password(), changePasswordDTO.newPassword());
        return ResponseEntity.ok().build();
    }
}