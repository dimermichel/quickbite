package com.michelmaia.quickbite.application.usecase.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.exception.MenuItemNotFoundException;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;

/**
 * Use Case: Get menu item by ID
 */
public class GetMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;

    public GetMenuItemUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem execute(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuItemNotFoundException(menuItemId));
    }
}