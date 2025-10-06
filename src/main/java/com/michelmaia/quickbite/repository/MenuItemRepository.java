package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.MenuItemDTO;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    Optional<MenuItemDTO> findById(Long id);

    List<MenuItemDTO> findByNameByRestaurantId(String name, Long restaurantId);

    List<MenuItemDTO> findAllAvailableByRestaurantId(Boolean available, Long restaurantId);

    List<MenuItemDTO> findAllByRestaurantId(Long restaurantId);

    MenuItemDTO save(MenuItemDTO menuItem);

    MenuItemDTO update(MenuItemDTO menuItem);

    Integer deleteById(Long id);
}
