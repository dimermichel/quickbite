package com.michelmaia.quickbite.domain.menuitem.exception;

/**
 * Domain exception when menu item is not found
 */
public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }

    public MenuItemNotFoundException(Long id) {
        super("Menu item not found with id: " + id);
    }
}