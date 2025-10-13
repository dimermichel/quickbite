package com.michelmaia.quickbite.domain.restaurant.exception;

/**
 * Domain exception when user tries to access/modify restaurant they don't own
 */
public class UnauthorizedRestaurantAccessException extends RuntimeException {
    public UnauthorizedRestaurantAccessException(String message) {
        super(message);
    }

    public UnauthorizedRestaurantAccessException() {
        super("You don't have permission to access this restaurant");
    }
}