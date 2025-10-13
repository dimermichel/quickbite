package com.michelmaia.quickbite.presentation.rest.common;

import com.michelmaia.quickbite.domain.auth.exception.AccountDisabledException;
import com.michelmaia.quickbite.domain.auth.exception.InvalidCredentialsException;
import com.michelmaia.quickbite.domain.menuitem.exception.InvalidMenuItemDataException;
import com.michelmaia.quickbite.domain.menuitem.exception.MenuItemNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.exception.InvalidRestaurantDataException;
import com.michelmaia.quickbite.domain.restaurant.exception.RestaurantNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.michelmaia.quickbite.domain.restaurant.exception.UnauthorizedRestaurantOwnerException;
import com.michelmaia.quickbite.domain.user.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers
 * Converts domain exceptions to appropriate HTTP responses
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // ========== Authentication Exceptions ==========
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        LOGGER.warn("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ErrorDTO> handleAccountDisabled(AccountDisabledException ex) {
        LOGGER.warn("Account disabled: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    // ========== User Exceptions ==========
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFound(UserNotFoundException ex) {
        LOGGER.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        LOGGER.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(UserHasRestaurantsException.class)
    public ResponseEntity<ErrorDTO> handleUserHasRestaurants(UserHasRestaurantsException ex) {
        LOGGER.warn("User has restaurants: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorDTO> handleInvalidUserData(InvalidUserDataException ex) {
        LOGGER.warn("Invalid user data: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    // ========== Validation Exceptions ==========
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        LOGGER.warn("Validation errors: {}", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }
    
    // ========== Generic Exceptions ==========
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDTO> handleIllegalState(IllegalStateException ex) {
        LOGGER.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorDTO(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        LOGGER.error("Unexpected error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorDTO("An unexpected error occurred"));
    }

    // ========== Restaurant Exceptions ==========

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        LOGGER.warn("Restaurant not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidRestaurantDataException.class)
    public ResponseEntity<ErrorDTO> handleInvalidRestaurantData(InvalidRestaurantDataException ex) {
        LOGGER.warn("Invalid restaurant data: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedRestaurantAccessException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorizedRestaurantAccess(UnauthorizedRestaurantAccessException ex) {
        LOGGER.warn("Unauthorized restaurant access: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedRestaurantOwnerException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorizedRestaurantOwnerException(UnauthorizedRestaurantOwnerException ex) {
        LOGGER.warn("Unauthorized restaurant owner: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDTO(ex.getMessage()));
    }

    // ========== Menu Item Exceptions ==========

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleMenuItemNotFound(MenuItemNotFoundException ex) {
        LOGGER.warn("Menu item not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidMenuItemDataException.class)
    public ResponseEntity<ErrorDTO> handleInvalidMenuItemData(InvalidMenuItemDataException ex) {
        LOGGER.warn("Invalid menu item data: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(ex.getMessage()));
    }
}
