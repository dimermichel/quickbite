package com.michelmaia.quickbite.domain.user.exception;

/**
 * Domain exception when attempting to delete a user who owns restaurants
 */
public class UserHasRestaurantsException extends RuntimeException {
    private final long restaurantCount;
    
    public UserHasRestaurantsException(String message, long restaurantCount) {
        super(message);
        this.restaurantCount = restaurantCount;
    }
    
    public UserHasRestaurantsException(long restaurantCount) {
        this("Cannot delete user. User owns " + restaurantCount + " restaurant(s)", restaurantCount);
    }
    
    public long getRestaurantCount() {
        return restaurantCount;
    }
}
