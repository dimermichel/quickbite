package com.michelmaia.quickbite.domain.auth.exception;

/**
 * Domain exception for invalid credentials
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
