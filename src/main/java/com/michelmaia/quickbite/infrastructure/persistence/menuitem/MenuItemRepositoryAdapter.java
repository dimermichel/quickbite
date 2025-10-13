package com.michelmaia.quickbite.infrastructure.persistence.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure Adapter: Implements MenuItemRepository using JDBC
 */
@Repository
public class MenuItemRepositoryAdapter implements MenuItemRepository {

    private final JdbcClient jdbcClient;
    private final MenuItemJdbcMapper mapper;

    public MenuItemRepositoryAdapter(JdbcClient jdbcClient, MenuItemJdbcMapper mapper) {
        this.jdbcClient = jdbcClient;
        this.mapper = mapper;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        if (menuItem.isNew()) {
            return insert(menuItem);
        } else {
            return update(menuItem);
        }
    }

    private MenuItem insert(MenuItem menuItem) {
        Long menuItemId = jdbcClient.sql("""
                INSERT INTO menu_items (restaurant_id, name, description, price, image_url, is_available)
                VALUES (:restaurantId, :name, :description, :price, :imageUrl, :isAvailable)
                RETURNING id
            """)
                .param("restaurantId", menuItem.getRestaurantId())
                .param("name", menuItem.getName())
                .param("description", menuItem.getDescription())
                .param("price", menuItem.getPrice())
                .param("imageUrl", menuItem.getImageUrl())
                .param("isAvailable", menuItem.isAvailable())
                .query(Long.class)
                .single();

        return findById(menuItemId).orElseThrow();
    }

    private MenuItem update(MenuItem menuItem) {
        jdbcClient.sql("""
                UPDATE menu_items
                SET name = :name, description = :description, price = :price,
                    image_url = :imageUrl, is_available = :isAvailable, 
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
            """)
                .param("name", menuItem.getName())
                .param("description", menuItem.getDescription())
                .param("price", menuItem.getPrice())
                .param("imageUrl", menuItem.getImageUrl())
                .param("isAvailable", menuItem.isAvailable())
                .param("id", menuItem.getId())
                .update();

        return findById(menuItem.getId()).orElseThrow();
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return jdbcClient.sql("""
                SELECT id, restaurant_id, name, description, price, image_url, 
                       is_available, created_at, updated_at
                FROM menu_items
                WHERE id = :id
            """)
                .param("id", id)
                .query(mapper::mapRow)
                .optional();
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return jdbcClient.sql("""
                SELECT id, restaurant_id, name, description, price, image_url, 
                       is_available, created_at, updated_at
                FROM menu_items
                WHERE restaurant_id = :restaurantId
                ORDER BY created_at DESC
            """)
                .param("restaurantId", restaurantId)
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public List<MenuItem> findByRestaurantIdAndAvailability(Long restaurantId, boolean isAvailable) {
        return jdbcClient.sql("""
                SELECT id, restaurant_id, name, description, price, image_url, 
                       is_available, created_at, updated_at
                FROM menu_items
                WHERE restaurant_id = :restaurantId AND is_available = :isAvailable
                ORDER BY created_at DESC
            """)
                .param("restaurantId", restaurantId)
                .param("isAvailable", isAvailable)
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public List<MenuItem> findByRestaurantIdAndNameContaining(Long restaurantId, String name) {
        return jdbcClient.sql("""
                SELECT id, restaurant_id, name, description, price, image_url, 
                       is_available, created_at, updated_at
                FROM menu_items
                WHERE restaurant_id = :restaurantId 
                  AND LOWER(name) LIKE LOWER(:name)
                ORDER BY name
            """)
                .param("restaurantId", restaurantId)
                .param("name", "%" + name + "%")
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public void delete(MenuItem menuItem) {
        jdbcClient.sql("DELETE FROM menu_items WHERE id = :id")
                .param("id", menuItem.getId())
                .update();
    }

    @Override
    public boolean existsById(Long id) {
        return jdbcClient.sql("SELECT COUNT(*) FROM menu_items WHERE id = :id")
                .param("id", id)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public long countByRestaurantId(Long restaurantId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM menu_items WHERE restaurant_id = :restaurantId")
                .param("restaurantId", restaurantId)
                .query(Long.class)
                .single();
    }
}