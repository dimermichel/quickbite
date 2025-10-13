package com.michelmaia.quickbite.domain.restaurant.exception;

/**
 * Domain exception when restaurant is not found
 */
public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(Long id) {
        super("Restaurant not found with id: " + id);
    }
}