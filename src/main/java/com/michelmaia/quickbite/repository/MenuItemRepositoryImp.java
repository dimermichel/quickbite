package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.MenuItemDTO;
import com.michelmaia.quickbite.model.MenuItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MenuItemRepositoryImp implements MenuItemRepository {

    private final JdbcClient jdbcClient;

    public MenuItemRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<MenuItemDTO> findById(Long id) {
        var menuItem = jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at 
                        FROM menu_items WHERE id = :id
                        """)
                .param("id", id)
                .query(MenuItem.class)
                .optional();

        return menuItem.map(this::toDTO);
    }

    @Override
    public List<MenuItemDTO> findByNameByRestaurantId(String name, Long restaurantId) {
        var menuItems = jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at
                        FROM menu_items WHERE LOWER(name) LIKE LOWER(:name) AND restaurant_id = :restaurantId
                        """)
                .param("name", "%" + name + "%")
                .param("restaurantId", restaurantId)
                .query(MenuItem.class)
                .list();
        return menuItems.stream().map(this::toDTO).toList();
    }

    @Override
    public List<MenuItemDTO> findAllAvailableByRestaurantId(Boolean isAvailable, Long restaurantId) {
        var menuItems = jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at
                        FROM menu_items WHERE is_available = :isAvailable AND restaurant_id = :restaurantId
                        """)
                .param("isAvailable", isAvailable)
                .param("restaurantId", restaurantId)
                .query(MenuItem.class)
                .list();
        return menuItems.stream().map(this::toDTO).toList();
    }

    @Override
    public List<MenuItemDTO> findAllByRestaurantId(Long restaurantId) {
        var menuItems = jdbcClient.sql("""
                        SELECT id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at
                        FROM menu_items WHERE restaurant_id = :restaurantId
                        """)
                .param("restaurantId", restaurantId)
                .query(MenuItem.class)
                .list();
        return menuItems.stream().map(this::toDTO).toList();
    }

    @Override
    public MenuItemDTO save(MenuItemDTO menuItem) {
        var savedMenuItem = jdbcClient.sql("""
                        INSERT INTO menu_items (restaurant_id, name, description, price, image_url, is_available, created_at, updated_at)
                        VALUES (:restaurantId, :name, :description, :price, :imageUrl, :isAvailable, NOW(), NOW())
                        RETURNING id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at
                        """)
                .param("restaurantId", menuItem.restaurantId())
                .param("name", menuItem.name())
                .param("description", menuItem.description())
                .param("price", menuItem.price())
                .param("imageUrl", menuItem.imageUrl())
                .param("isAvailable", menuItem.isAvailable())
                .query(MenuItem.class)
                .optional()
                .orElseThrow(() -> new IllegalStateException("Failed to save menu item"));
        return toDTO(savedMenuItem);
    }

    @Override
    public MenuItemDTO update(MenuItemDTO menuItem) {
        var updatedMenuItem = jdbcClient.sql("""
                        UPDATE menu_items SET restaurant_id = :restaurantId, name = :name, description = :description,
                        price = :price, image_url = :imageUrl, is_available = :isAvailable, updated_at = NOW() WHERE id = :id
                        RETURNING id, restaurant_id, name, description, price, image_url, is_available, created_at, updated_at
                        """)
                .param("restaurantId", menuItem.restaurantId())
                .param("name", menuItem.name())
                .param("description", menuItem.description())
                .param("price", menuItem.price())
                .param("imageUrl", menuItem.imageUrl())
                .param("isAvailable", menuItem.isAvailable())
                .param("id", menuItem.id())
                .query(MenuItem.class)
                .optional()
                .orElseThrow(() -> new IllegalStateException("Failed to save menu item"));
        return toDTO(updatedMenuItem);
    }

    @Override
    public Integer deleteById(Long id) {
        return jdbcClient.sql("DELETE FROM menu_items WHERE id = :id")
                .param("id", id)
                .update();
    }

    private MenuItemDTO toDTO(MenuItem menuItem) {
        return new MenuItemDTO(
                menuItem.getId(),
                menuItem.getRestaurantId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getImageUrl(),
                menuItem.getIsAvailable(),
                menuItem.getCreatedAt(),
                menuItem.getUpdatedAt()
        );
    }
}
