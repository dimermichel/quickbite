package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import com.michelmaia.quickbite.mapper.RestaurantRowMapper;
import com.michelmaia.quickbite.model.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RestaurantRepositoryImp implements RestaurantRepository {

    private final JdbcClient jdbcClient;
    private final RestaurantRowMapper restaurantRowMapper;

    public RestaurantRepositoryImp(JdbcClient jdbcClient, RestaurantRowMapper restaurantRowMapper) {
        this.jdbcClient = jdbcClient;
        this.restaurantRowMapper = restaurantRowMapper;
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        Optional<Restaurant> restaurantOpt = jdbcClient.sql("""
                            SELECT r.id, r.owner_id, r.name, r.cuisine, r.rating, r.opening_hours
                                   r.is_open, r.created_at, r.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM restaurants r
                            LEFT JOIN addresses a ON r.address_id = a.id
                            WHERE r.id = :id
                        """)
                .param("id", id)
                .query(restaurantRowMapper)
                .optional();
        return restaurantOpt;
    }

    @Override
    public Optional<Restaurant> findByOwnerId(Long ownerId) {
        Optional<Restaurant> restaurantOpt = jdbcClient.sql("""
                            SELECT r.id, r.owner_id, r.name, r.cuisine, r.rating, r.opening_hours
                                   r.is_open, r.created_at, r.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM restaurants r
                            LEFT JOIN addresses a ON r.address_id = a.id
                            WHERE r.owner_id = :ownerId
                        """)
                .param("ownerId", ownerId)
                .query(restaurantRowMapper)
                .optional();
        return restaurantOpt;
    }

    @Override
    public PageResponseDTO<Restaurant> findByCuisine(Pageable pageable, String cuisine) {
        //window function to get both the data and the total count in a single query
        var restaurants = jdbcClient.sql("""
                            SELECT r.id, r.owner_id, r.name, r.cuisine, r.rating, r.opening_hours,
                                   r.is_open, r.created_at, r.updated_at,
                                   a.street, a.city, a.state, a.zip_code,
                                   COUNT(*) OVER() as total_count
                            FROM restaurants r
                            LEFT JOIN addresses a ON r.address_id = a.id
                            WHERE r.cuisine = :cuisine
                            ORDER BY r.name
                            LIMIT :limit OFFSET :offset
                        """)
                .param("cuisine", cuisine)
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(restaurantRowMapper)
                .list();

        Long total = restaurants.isEmpty() ? 0L : restaurants.get(0).getTotalCount();

        return new PageResponseDTO<>(
                restaurants,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );
    }

    @Override
    public PageResponseDTO<Restaurant> findByRating(Pageable pageable, Double minRating) {
        var restaurants = jdbcClient.sql("""
                            SELECT r.id, r.owner_id, r.name, r.cuisine, r.rating, r.opening_hours,
                                   r.is_open, r.created_at, r.updated_at,
                                   a.street, a.city, a.state, a.zip_code,
                                   COUNT(*) OVER() as total_count
                            FROM restaurants r
                            LEFT JOIN addresses a ON r.address_id = a.id
                            WHERE r.rating >= :minRating
                            ORDER BY r.rating DESC
                            LIMIT :limit OFFSET :offset
                        """)
                .param("minRating", minRating)
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(restaurantRowMapper)
                .list();

        Long total = restaurants.isEmpty() ? 0L : restaurants.get(0).getTotalCount();

        return new PageResponseDTO<>(
                restaurants,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );
    }

    @Override
    public PageResponseDTO<Restaurant> findAllPaginated(Pageable pageable) {
        var restaurants = jdbcClient.sql("""
                            SELECT r.id, r.owner_id, r.name, r.cuisine, r.rating, r.opening_hours,
                                   r.is_open, r.created_at, r.updated_at,
                                   a.street, a.city, a.state, a.zip_code,
                                   COUNT(*) OVER() as total_count
                            FROM restaurants r
                            LEFT JOIN addresses a ON r.address_id = a.id
                            ORDER BY r.name
                            LIMIT :limit OFFSET :offset
                        """)
                .param("limit", pageable.getPageSize())
                .param("offset", pageable.getOffset())
                .query(restaurantRowMapper)
                .list();

        Long total = restaurants.isEmpty() ? 0L : restaurants.get(0).getTotalCount();

        return new PageResponseDTO<>(
                restaurants,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );
    }

    @Override
    public Restaurant save(RestaurantDTO restaurant) {
        // Insert the address first (if any)
        Long addressId = null;
        if (restaurant.address() != null) {
            addressId = jdbcClient.sql("""
                                INSERT INTO addresses (street, city, state, zip_code)
                                VALUES (:street, :city, :state, :zipCode)
                                RETURNING id
                            """)
                    .param("street", restaurant.address().getStreet())
                    .param("city", restaurant.address().getCity())
                    .param("state", restaurant.address().getState())
                    .param("zipCode", restaurant.address().getZipCode())
                    .query(Long.class)
                    .single();
        }
        // Insert the restaurant
        Long restaurantId = jdbcClient.sql("""
                            INSERT INTO restaurants (owner_id, address_id, name, cuisine, rating, opening_hours, is_open, created_at, updated_at)
                            VALUES (:ownerId, :addressId, :name, :cuisine, :rating, :openingHours, :isOpen, NOW(), NOW())
                            RETURNING id
                        """)
                .param("ownerId", restaurant.ownerId())
                .param("addressId", addressId)
                .param("name", restaurant.name())
                .param("cuisine", restaurant.cuisine())
                .param("rating", restaurant.rating())
                .param("openingHours", restaurant.openingHours())
                .param("isOpen", restaurant.isOpen())
                .query(Long.class)
                .single();

        return findById(restaurantId).orElseThrow();
    }

    @Override
    public Restaurant update(RestaurantDTO restaurant) {
        // Handle address update/creation
        if (restaurant.address() != null) {
            // First, check if the restaurant already has an address
            Optional<Long> existingAddressId = jdbcClient.sql(
                            "SELECT address_id FROM restaurants WHERE id = :restaurantId")
                    .param("restaurantId", restaurant.id())
                    .query(Long.class)
                    .optional();

            if (existingAddressId.isPresent() && existingAddressId.get() != null) {
                // Update existing address
                jdbcClient.sql("""
                                    UPDATE addresses
                                    SET street = :street, city = :city, state = :state, zip_code = :zipCode
                                    WHERE id = :addressId
                                """)
                        .param("street", restaurant.address().getStreet())
                        .param("city", restaurant.address().getCity())
                        .param("state", restaurant.address().getState())
                        .param("zipCode", restaurant.address().getZipCode())
                        .param("addressId", existingAddressId.get())
                        .update();
            } else {
                // Create a new address and link it to the restaurant
                Long newAddressId = jdbcClient.sql("""
                                    INSERT INTO addresses (street, city, state, zip_code)
                                    VALUES (:street, :city, :state, :zipCode)
                                    RETURNING id
                                """)
                        .param("street", restaurant.address().getStreet())
                        .param("city", restaurant.address().getCity())
                        .param("state", restaurant.address().getState())
                        .param("zipCode", restaurant.address().getZipCode())
                        .query(Long.class)
                        .single();

                // Update the restaurant to reference the new address
                jdbcClient.sql("UPDATE restaurant SET address_id = :addressId WHERE id = :restaurantId")
                        .param("addressId", newAddressId)
                        .param("restaurantId", restaurant.id())
                        .update();
            }
        }

        // Now update the restaurant details
        int rowsAffected = jdbcClient.sql("""
                                UPDATE restaurants
                                SET name = :name, cuisine = :cuisine, rating = :rating,
                                    opening_hours = :openingHours, is_open = :isOpen, updated_at = NOW()
                                WHERE id = :id
                            """)
                .param("name", restaurant.name())
                .param("cuisine", restaurant.cuisine())
                .param("rating", restaurant.rating())
                .param("openingHours", restaurant.openingHours())
                .param("isOpen", restaurant.isOpen())
                .param("id", restaurant.id())
                .update();
        if (rowsAffected == 0) {
            throw new IllegalStateException("Restaurant with id " + restaurant.id() + " could not be updated");
        } else {
            return findById(restaurant.id()).orElseThrow();
        }
    }

    @Override
    public Integer deleteById(Long id) {
        Optional<Long> addressIdOpt = jdbcClient.sql("SELECT address_id FROM restaurants WHERE id = :id")
                .param("id", id)
                .query(Long.class)
                .optional();

        int rows = jdbcClient.sql("DELETE FROM restaurants WHERE id = :id")
                .param("id", id)
                .update();

        addressIdOpt.ifPresent(addressId -> {
            if (addressId != null) {
                jdbcClient.sql("DELETE FROM addresses WHERE id = :addressId")
                        .param("addressId", addressId)
                        .update();
            }
        });

        return rows;
    }
}
