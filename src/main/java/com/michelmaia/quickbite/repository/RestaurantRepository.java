package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import com.michelmaia.quickbite.model.Restaurant;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findByOwnerId(Long ownerId);

    PageResponseDTO<Restaurant> findByCuisine(Pageable pageable, String cuisine);

    PageResponseDTO<Restaurant> findByRating(Pageable pageable, Double minRating);

    PageResponseDTO<Restaurant> findAllPaginated(Pageable pageable);

    Restaurant save(RestaurantDTO restaurant);

    Restaurant update(RestaurantDTO restaurant);

    Integer deleteById(Long id);
}
