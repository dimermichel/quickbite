package com.michelmaia.quickbite.domain.menuitem.repository;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository Interface
 * Defined by the domain, implemented by infrastructure
 */
public interface MenuItemRepository {

    MenuItem save(MenuItem menuItem);

    Optional<MenuItem> findById(Long id);

    List<MenuItem> findByRestaurantId(Long restaurantId);

    List<MenuItem> findByRestaurantIdAndAvailability(Long restaurantId, boolean isAvailable);

    List<MenuItem> findByRestaurantIdAndNameContaining(Long restaurantId, String name);

    void delete(MenuItem menuItem);

    boolean existsById(Long id);

    long countByRestaurantId(Long restaurantId);
}