package com.michelmaia.quickbite.application.usecase.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.exception.MenuItemNotFoundException;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;

/**
 * Use Case: Update menu item information
 */
public class UpdateMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;

    public UpdateMenuItemUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem execute(UpdateMenuItemCommand command) {
        // Find existing menu item
        MenuItem menuItem = menuItemRepository.findById(command.menuItemId())
                .orElseThrow(() -> new MenuItemNotFoundException(command.menuItemId()));

        // Update info
        menuItem.updateInfo(
                command.name(),
                command.description(),
                command.price(),
                command.imageUrl()
        );

        // Update availability if specified
        if (command.isAvailable() != null) {
            if (command.isAvailable()) {
                menuItem.markAsAvailable();
            } else {
                menuItem.markAsUnavailable();
            }
        }

        return menuItemRepository.save(menuItem);
    }

    public record UpdateMenuItemCommand(
            Long menuItemId,
            String name,
            String description,
            Double price,
            String imageUrl,
            Boolean isAvailable
    ) {}
}