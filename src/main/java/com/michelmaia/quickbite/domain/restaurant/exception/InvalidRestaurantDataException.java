
package com.michelmaia.quickbite.domain.restaurant.exception;

/**
 * Domain exception for invalid restaurant data
 */
public class InvalidRestaurantDataException extends RuntimeException {
    public InvalidRestaurantDataException(String message) {
        super(message);
    }
}