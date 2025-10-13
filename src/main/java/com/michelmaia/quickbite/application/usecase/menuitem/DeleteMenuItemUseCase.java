
package com.michelmaia.quickbite.application.usecase.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.exception.MenuItemNotFoundException;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;

/**
 * Use Case: Delete a menu item
 */
public class DeleteMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;

    public DeleteMenuItemUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public void execute(Long menuItemId) {
        // Find menu item
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));

        // Delete menu item
        menuItemRepository.delete(menuItem);
    }
}