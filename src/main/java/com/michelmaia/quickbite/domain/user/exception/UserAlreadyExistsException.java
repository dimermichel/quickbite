package com.michelmaia.quickbite.domain.user.exception;

/**
 * Domain exception when user already exists
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
