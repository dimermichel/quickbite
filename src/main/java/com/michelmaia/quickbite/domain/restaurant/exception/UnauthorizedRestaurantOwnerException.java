package com.michelmaia.quickbite.domain.restaurant.exception;

public class UnauthorizedRestaurantOwnerException extends RuntimeException {
    public UnauthorizedRestaurantOwnerException(String message) {
        super(message);
    }
}
