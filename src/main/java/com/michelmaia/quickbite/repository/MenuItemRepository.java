package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.MenuItemDTO;
import com.michelmaia.quickbite.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    Optional<MenuItem> findById(Long id);

    Optional<MenuItem> findByNameByRestaurantId(String name, Long restaurantId);

    List<MenuItem> findAllAvailableByRestaurantId(Boolean available, Long restaurantId);

    List<MenuItem> findAllByRestaurantId(Long restaurantId);

    MenuItem save(MenuItemDTO menuItem);

    MenuItem update(MenuItemDTO menuItem);

    Integer deleteById(Long id);
}
