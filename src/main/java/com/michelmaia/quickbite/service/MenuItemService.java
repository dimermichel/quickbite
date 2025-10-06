package com.michelmaia.quickbite.service;

import com.michelmaia.quickbite.dto.MenuItemDTO;
import com.michelmaia.quickbite.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public Optional<MenuItemDTO> findMenuItemById(Long id){
        return menuItemRepository.findById(id);
    }

    public List<MenuItemDTO> findAllAvailableByRestaurant(Boolean available, Long restaurantId) {
        return menuItemRepository.findAllAvailableByRestaurantId(available, restaurantId);
    }

    public List<MenuItemDTO> findAllByRestaurant(Long restaurantId) {
        return menuItemRepository.findAllByRestaurantId(restaurantId);
    }

    public List<MenuItemDTO> findByNameByRestaurant(String name, Long restaurantId){
        return menuItemRepository.findByNameByRestaurantId(name, restaurantId);
    }

    public void saveMenuItem(MenuItemDTO menuItem) {
        var savedMenuItem = menuItemRepository.save(menuItem);
        if (savedMenuItem == null) {
            throw new IllegalStateException("Menu item could not be registered");
        }
    }

    public void updateMenuItem(MenuItemDTO menuItem) {
        var updatedMenuItem = menuItemRepository.update(menuItem);
        if (updatedMenuItem == null) {
            throw new IllegalStateException("Menu item could not be updated");
        }
    }

    public void deleteMenuItem(Long id) {
        Integer deleted = menuItemRepository.deleteById(id);
        if (deleted == null || deleted == 0) {
            throw new IllegalStateException("Menu item could not be deleted");
        }
    }

}
