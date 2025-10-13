package com.michelmaia.quickbite.domain.auth.exception;

/**
 * Domain exception when user account is disabled
 */
public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException(String message) {
        super(message);
    }
    
    public AccountDisabledException() {
        super("User account is disabled");
    }
}
