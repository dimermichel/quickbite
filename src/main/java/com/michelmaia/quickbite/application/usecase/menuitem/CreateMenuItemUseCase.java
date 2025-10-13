package com.michelmaia.quickbite.application.usecase.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;
import com.michelmaia.quickbite.domain.restaurant.exception.RestaurantNotFoundException;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;

/**
 * Use Case: Create a new menu item
 */
public class CreateMenuItemUseCase {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public CreateMenuItemUseCase(MenuItemRepository menuItemRepository,
                                 RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public MenuItem execute(CreateMenuItemCommand command) {
        // Business rule: Restaurant must exist
        restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(command.restaurantId()));

        // Create menu item
        MenuItem menuItem = MenuItem.createNew(
                command.restaurantId(),
                command.name(),
                command.description(),
                command.price(),
                command.imageUrl()
        );

        // Set availability if specified (default is true)
        if (command.isAvailable() != null && !command.isAvailable()) {
            menuItem.markAsUnavailable();
        }

        return menuItemRepository.save(menuItem);
    }

    public record CreateMenuItemCommand(
            Long restaurantId,
            String name,
            String description,
            Double price,
            String imageUrl,
            Boolean isAvailable
    ) {}
}