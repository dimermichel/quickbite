
package com.michelmaia.quickbite.domain.menuitem.exception;

/**
 * Domain exception for invalid menu item data
 */
public class InvalidMenuItemDataException extends RuntimeException {
    public InvalidMenuItemDataException(String message) {
        super(message);
    }
}