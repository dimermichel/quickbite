package com.michelmaia.quickbite.infrastructure.persistence.restaurant;

import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure Adapter: Implements RestaurantRepository using JDBC
 */
@Repository
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final JdbcClient jdbcClient;
    private final RestaurantJdbcMapper mapper;

    public RestaurantRepositoryAdapter(JdbcClient jdbcClient, RestaurantJdbcMapper mapper) {
        this.jdbcClient = jdbcClient;
        this.mapper = mapper;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        if (restaurant.isNew()) {
            return insert(restaurant);
        } else {
            return update(restaurant);
        }
    }

    private Restaurant insert(Restaurant restaurant) {
        // Insert address first
        Long addressId = jdbcClient.sql("""
                INSERT INTO addresses (street, city, state, zip_code)
                VALUES (:street, :city, :state, :zipCode)
                RETURNING id
            """)
                .param("street", restaurant.getAddress().getStreet())
                .param("city", restaurant.getAddress().getCity())
                .param("state", restaurant.getAddress().getState())
                .param("zipCode", restaurant.getAddress().getZipCode())
                .query(Long.class)
                .single();

        // Insert restaurant
        Long restaurantId = jdbcClient.sql("""
                INSERT INTO restaurants (owner_id, name, cuisine, address_id, opening_hours, rating, is_open)
                VALUES (:ownerId, :name, :cuisine, :addressId, :openingHours, :rating, :isOpen)
                RETURNING id
            """)
                .param("ownerId", restaurant.getOwnerId())
                .param("name", restaurant.getName())
                .param("cuisine", restaurant.getCuisine())
                .param("addressId", addressId)
                .param("openingHours", restaurant.getOpeningHours())
                .param("rating", restaurant.getRating())
                .param("isOpen", restaurant.isOpen())
                .query(Long.class)
                .single();

        return findById(restaurantId).orElseThrow();
    }

    private Restaurant update(Restaurant restaurant) {
        // Update address
        jdbcClient.sql("""
                UPDATE addresses
                SET street = :street, city = :city, state = :state, zip_code = :zipCode
                WHERE id = (SELECT address_id FROM restaurants WHERE id = :restaurantId)
            """)
                .param("street", restaurant.getAddress().getStreet())
                .param("city", restaurant.getAddress().getCity())
                .param("state", restaurant.getAddress().getState())
                .param("zipCode", restaurant.getAddress().getZipCode())
                .param("restaurantId", restaurant.getId())
                .update();

        // Update restaurant
        jdbcClient.sql("""
                UPDATE restaurants
                SET name = :name, cuisine = :cuisine, opening_hours = :openingHours,
                    rating = :rating, is_open = :isOpen, updated_at = CURRENT_TIMESTAMP
                WHERE id = :id
            """)
                .param("name", restaurant.getName())
                .param("cuisine", restaurant.getCuisine())
                .param("openingHours", restaurant.getOpeningHours())
                .param("rating", restaurant.getRating())
                .param("isOpen", restaurant.isOpen())
                .param("id", restaurant.getId())
                .update();

        return findById(restaurant.getId()).orElseThrow();
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return jdbcClient.sql("""
                SELECT r.id, r.owner_id, r.name, r.cuisine, r.opening_hours, r.rating, r.is_open,
                       r.created_at, r.updated_at,
                       a.street, a.city, a.state, a.zip_code
                FROM restaurants r
                LEFT JOIN addresses a ON r.address_id = a.id
                WHERE r.id = :id
            """)
                .param("id", id)
                .query(mapper::mapRow)
                .optional();
    }

    @Override
    public Optional<Restaurant> findByOwnerId(Long ownerId) {
        return jdbcClient.sql("""
                SELECT r.id, r.owner_id, r.name, r.cuisine, r.opening_hours, r.rating, r.is_open,
                       r.created_at, r.updated_at,
                       a.street, a.city, a.state, a.zip_code
                FROM restaurants r
                LEFT JOIN addresses a ON r.address_id = a.id
                WHERE r.owner_id = :ownerId
            """)
                .param("ownerId", ownerId)
                .query(mapper::mapRow)
                .optional();
    }

    @Override
    public List<Restaurant> findAll(int page, int size) {
        int offset = page * size;
        return jdbcClient.sql("""
                SELECT r.id, r.owner_id, r.name, r.cuisine, r.opening_hours, r.rating, r.is_open,
                       r.created_at, r.updated_at,
                       a.street, a.city, a.state, a.zip_code
                FROM restaurants r
                LEFT JOIN addresses a ON r.address_id = a.id
                ORDER BY r.created_at DESC
                LIMIT :size OFFSET :offset
            """)
                .param("size", size)
                .param("offset", offset)
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public List<Restaurant> findByCuisine(String cuisine, int page, int size) {
        int offset = page * size;
        return jdbcClient.sql("""
                SELECT r.id, r.owner_id, r.name, r.cuisine, r.opening_hours, r.rating, r.is_open,
                       r.created_at, r.updated_at,
                       a.street, a.city, a.state, a.zip_code
                FROM restaurants r
                LEFT JOIN addresses a ON r.address_id = a.id
                WHERE LOWER(r.cuisine) = LOWER(:cuisine)
                ORDER BY r.created_at DESC
                LIMIT :size OFFSET :offset
            """)
                .param("cuisine", cuisine)
                .param("size", size)
                .param("offset", offset)
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public List<Restaurant> findByMinRating(Double minRating, int page, int size) {
        int offset = page * size;
        return jdbcClient.sql("""
                SELECT r.id, r.owner_id, r.name, r.cuisine, r.opening_hours, r.rating, r.is_open,
                       r.created_at, r.updated_at,
                       a.street, a.city, a.state, a.zip_code
                FROM restaurants r
                LEFT JOIN addresses a ON r.address_id = a.id
                WHERE r.rating >= :minRating
                ORDER BY r.rating DESC
                LIMIT :size OFFSET :offset
            """)
                .param("minRating", minRating)
                .param("size", size)
                .param("offset", offset)
                .query(mapper::mapRow)
                .list();
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT COUNT(*) FROM restaurants")
                .query(Long.class)
                .single();
    }

    @Override
    public long countByCuisine(String cuisine) {
        return jdbcClient.sql("SELECT COUNT(*) FROM restaurants WHERE LOWER(cuisine) = LOWER(:cuisine)")
                .param("cuisine", cuisine)
                .query(Long.class)
                .single();
    }

    @Override
    public long countByMinRating(Double minRating) {
        return jdbcClient.sql("SELECT COUNT(*) FROM restaurants WHERE rating >= :minRating")
                .param("minRating", minRating)
                .query(Long.class)
                .single();
    }

    @Override
    public void delete(Restaurant restaurant) {
        // Get address ID
        Optional<Long> addressIdOpt = jdbcClient.sql("SELECT address_id FROM restaurants WHERE id = :id")
                .param("id", restaurant.getId())
                .query(Long.class)
                .optional();

        // Delete restaurant (menu items will be cascade deleted by FK)
        jdbcClient.sql("DELETE FROM restaurants WHERE id = :id")
                .param("id", restaurant.getId())
                .update();

        // Delete address
        addressIdOpt.ifPresent(addressId -> {
            if (addressId != null) {
                jdbcClient.sql("DELETE FROM addresses WHERE id = :addressId")
                        .param("addressId", addressId)
                        .update();
            }
        });
    }

    @Override
    public boolean existsById(Long id) {
        return jdbcClient.sql("SELECT COUNT(*) FROM restaurants WHERE id = :id")
                .param("id", id)
                .query(Long.class)
                .single() > 0;
    }
}