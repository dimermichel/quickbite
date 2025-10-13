package com.michelmaia.quickbite.domain.user.exception;

/**
 * Domain exception for invalid user data
 */
public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
