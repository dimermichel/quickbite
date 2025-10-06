package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<RestaurantDTO> findById(Long id);

    Optional<RestaurantDTO> findByOwnerId(Long ownerId);

    PageResponseDTO<RestaurantDTO> findByCuisine(Pageable pageable, String cuisine);

    PageResponseDTO<RestaurantDTO> findByRating(Pageable pageable, Double minRating);

    PageResponseDTO<RestaurantDTO> findAllPaginated(Pageable pageable);

    RestaurantDTO save(RestaurantDTO restaurant);

    RestaurantDTO update(RestaurantDTO restaurant);

    Integer deleteById(Long id);
}
