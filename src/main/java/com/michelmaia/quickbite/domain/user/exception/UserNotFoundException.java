package com.michelmaia.quickbite.domain.user.exception;

/**
 * Domain exception when user is not found
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
