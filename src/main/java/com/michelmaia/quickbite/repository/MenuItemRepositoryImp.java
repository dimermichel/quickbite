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
    public Optional<MenuItem> findById(Long id) {
        var menuItem = jdbcClient.sql("SELECT * FROM menu_items WHERE id = :id")
                .param("id", id)
                .query(MenuItem.class)
                .optional();

        return menuItem;
    }

    @Override
    public Optional<MenuItem> findByNameByRestaurantId(String name, Long restaurantId) {
        var menuItem = jdbcClient.sql("SELECT * FROM menu_items WHERE name = :name")
                .param("name", name)
                .query(MenuItem.class)
                .optional();
        return menuItem;
    }

    @Override
    public List<MenuItem> findAllAvailableByRestaurantId(Boolean available, Long restaurantId) {
        var menuItems = jdbcClient.sql("SELECT * FROM menu_items WHERE available = :available AND restaurant_id = :restaurantId")
                .param("available", available)
                .param("restaurantId", restaurantId)
                .query(MenuItem.class)
                .list();
        return menuItems;
    }

    @Override
    public List<MenuItem> findAllByRestaurantId(Long restaurantId) {
        var menuItems = jdbcClient.sql("SELECT * FROM menu_items WHERE restaurant_id = :restaurantId")
                .param("restaurantId", restaurantId)
                .query(MenuItem.class)
                .list();
        return menuItems;
    }

    @Override
    public MenuItem save(MenuItemDTO menuItem) {
        var savedMenuItem = jdbcClient.sql("INSERT INTO menu_items (restaurant_id, name, description, price, image_url, available, created_at, updated_at) " +
                        "VALUES (:restaurantId, :name, :description, :price, :imageUrl, :available, NOW(), NOW()) RETURNING *")
                .param("restaurantId", menuItem.restaurantId())
                .param("name", menuItem.name())
                .param("description", menuItem.description())
                .param("price", menuItem.price())
                .param("imageUrl", menuItem.imageUrl())
                .param("available", menuItem.available())
                .query(MenuItem.class)
                .optional()
                .orElse(null);
        return savedMenuItem;
    }

    @Override
    public MenuItem update(MenuItemDTO menuItem) {
        var updatedMenuItem = jdbcClient.sql("UPDATE menu_items SET restaurant_id = :restaurantId, name = :name, description = :description, " +
                        "price = :price, image_url = :imageUrl, available = :available, updated_at = NOW() WHERE id = :id RETURNING *")
                .param("restaurantId", menuItem.restaurantId())
                .param("name", menuItem.name())
                .param("description", menuItem.description())
                .param("price", menuItem.price())
                .param("imageUrl", menuItem.imageUrl())
                .param("available", menuItem.available())
                .param("id", menuItem.id())
                .query(MenuItem.class)
                .optional()
                .orElse(null);
        return updatedMenuItem;
    }

    @Override
    public Integer deleteById(Long id) {
        int deleted = jdbcClient.sql("DELETE FROM menu_items WHERE id = :id")
                .param("id", id)
                .update();

        return deleted;
    }
}
