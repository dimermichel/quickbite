package com.michelmaia.quickbite.application.usecase.restaurant;

import com.michelmaia.quickbite.application.dto.PageResponseDTO;
import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;

import java.util.List;

/**
 * Use Case: List restaurants with pagination and filters
 */
public class ListRestaurantsUseCase {

    private final RestaurantRepository restaurantRepository;

    public ListRestaurantsUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public PageResponseDTO<Restaurant> execute(ListRestaurantsQuery query) {
        List<Restaurant> restaurants;
        long totalCount;

        if (query.cuisine() != null && !query.cuisine().isBlank()) {
            // Filter by cuisine
            restaurants = restaurantRepository.findByCuisine(
                    query.cuisine(),
                    query.page(),
                    query.size()
            );
            totalCount = restaurantRepository.countByCuisine(query.cuisine());
        } else if (query.minRating() != null) {
            // Filter by rating
            restaurants = restaurantRepository.findByMinRating(
                    query.minRating(),
                    query.page(),
                    query.size()
            );
            totalCount = restaurantRepository.countByMinRating(query.minRating());
        } else {
            // Get all
            restaurants = restaurantRepository.findAll(query.page(), query.size());
            totalCount = restaurantRepository.count();
        }

        return new PageResponseDTO<>(
                restaurants,
                query.page(),
                query.size(),
                totalCount
        );
    }

    public record ListRestaurantsQuery(
            int page,
            int size,
            String cuisine,
            Double minRating
    ) {
        public ListRestaurantsQuery {
            if (page < 0) {
                throw new IllegalArgumentException("Page must be non-negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be positive");
            }
            if (size > 100) {
                throw new IllegalArgumentException("Size must not exceed 100");
            }
        }

        // Convenience constructors
        public ListRestaurantsQuery(int page, int size) {
            this(page, size, null, null);
        }
    }
}