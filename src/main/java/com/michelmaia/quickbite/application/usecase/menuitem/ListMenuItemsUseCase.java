
package com.michelmaia.quickbite.application.usecase.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;

import java.util.List;

/**
 * Use Case: List menu items with filters
 */
public class ListMenuItemsUseCase {

    private final MenuItemRepository menuItemRepository;

    public ListMenuItemsUseCase(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> execute(ListMenuItemsQuery query) {
        // Filter by availability
        if (query.isAvailable() != null) {
            return menuItemRepository.findByRestaurantIdAndAvailability(
                    query.restaurantId(),
                    query.isAvailable()
            );
        }

        // Filter by name (search)
        if (query.nameSearch() != null && !query.nameSearch().isBlank()) {
            return menuItemRepository.findByRestaurantIdAndNameContaining(
                    query.restaurantId(),
                    query.nameSearch()
            );
        }

        // Get all for restaurant
        return menuItemRepository.findByRestaurantId(query.restaurantId());
    }

    public record ListMenuItemsQuery(
            Long restaurantId,
            Boolean isAvailable,
            String nameSearch
    ) {
        public ListMenuItemsQuery {
            if (restaurantId == null) {
                throw new IllegalArgumentException("Restaurant ID is required");
            }
        }

        // Convenience constructors
        public ListMenuItemsQuery(Long restaurantId) {
            this(restaurantId, null, null);
        }

        public ListMenuItemsQuery(Long restaurantId, Boolean isAvailable) {
            this(restaurantId, isAvailable, null);
        }
    }
}