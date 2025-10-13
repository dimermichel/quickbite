package com.michelmaia.quickbite.domain.restaurant.repository;

import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository Interface
 * Defined by the domain, implemented by infrastructure
 */
public interface RestaurantRepository {

    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findByOwnerId(Long ownerId);

    List<Restaurant> findAll(int page, int size);

    List<Restaurant> findByCuisine(String cuisine, int page, int size);

    List<Restaurant> findByMinRating(Double minRating, int page, int size);

    long count();

    long countByCuisine(String cuisine);

    long countByMinRating(Double minRating);

    void delete(Restaurant restaurant);

    boolean existsById(Long id);
}