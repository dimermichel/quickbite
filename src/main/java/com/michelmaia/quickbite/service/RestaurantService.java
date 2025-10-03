package com.michelmaia.quickbite.service;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import com.michelmaia.quickbite.model.Restaurant;
import com.michelmaia.quickbite.repository.RestaurantRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Optional<Restaurant> findUserById(Long id){
        return restaurantRepository.findById(id);
    }

    public Optional<Restaurant> findByOwnerId(Long ownerId){
        return restaurantRepository.findByOwnerId(ownerId);
    }

    public PageResponseDTO<Restaurant> findByCuisine(Pageable pageable, String cuisine) {
        return restaurantRepository.findByCuisine(pageable, cuisine);
    }

    public PageResponseDTO<Restaurant> findByRating(Pageable pageable, Double minRating) {
        return restaurantRepository.findByRating(pageable, minRating);
    }

    public PageResponseDTO<Restaurant> findAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAllPaginated(pageable);
    }

    public void saveRestaurant(RestaurantDTO restaurant) {
        var savedRestaurant = restaurantRepository.save(restaurant);
        if (savedRestaurant == null) {
            throw new IllegalStateException("Restaurant could not be registered");
        }
    }

    public void updateRestaurant(RestaurantDTO restaurant) {
        var updatedRestaurant = restaurantRepository.update(restaurant);
        if (updatedRestaurant == null) {
            throw new IllegalStateException("Restaurant could not be updated");
        }
    }

    public void deleteRestaurant(Long id) {
        Integer deleted = restaurantRepository.deleteById(id);
        if (deleted == null || deleted == 0) {
            throw new IllegalStateException("Restaurant could not be deleted");
        }
    }

}
