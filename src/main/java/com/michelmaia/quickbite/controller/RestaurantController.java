package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import com.michelmaia.quickbite.model.Restaurant;
import com.michelmaia.quickbite.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Restaurant Management", description = "Endpoints for managing restaurants")
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "Find all restaurants", description = "Fetches a paginated list of all restaurants",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of restaurants retrieved successfully")
            })
    @GetMapping
    public ResponseEntity<PageResponseDTO<Restaurant>> findAllRestaurants(Pageable pageable) {
        LOGGER.info("GET -> /api/restaurants - Fetching all restaurants - Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        PageResponseDTO<Restaurant> restaurants = restaurantService.findAllRestaurants(pageable);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Find restaurants by cuisine", description = "Fetches a paginated list of restaurants filtered by cuisine type",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of restaurants retrieved successfully")
            })
    @GetMapping("/by-cuisine")
    ResponseEntity<PageResponseDTO<Restaurant>> findRestaurantByCuisine(Pageable pageable, @RequestParam String cuisine) {
        LOGGER.info("GET -> /api/restaurants/by-cuisine - Fetching restaurants by cuisine: {} - Page: {}, Size: {}", cuisine, pageable.getPageNumber(), pageable.getPageSize());
        PageResponseDTO<Restaurant> restaurants = restaurantService.findByCuisine(pageable, cuisine);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Find restaurants by minimum rating", description = "Fetches a paginated list of restaurants with a minimum rating",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of restaurants retrieved successfully")
            })
    @GetMapping("/by-rating")
    ResponseEntity<PageResponseDTO<Restaurant>> findRestaurantByRating(Pageable pageable, @RequestParam Double minRating) {
        LOGGER.info("GET -> /api/restaurants/by-rating - Fetching restaurants by minimum rating: {} - Page: {}, Size: {}", minRating, pageable.getPageNumber(), pageable.getPageSize());
        PageResponseDTO<Restaurant> restaurants = restaurantService.findByRating(pageable, minRating);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "Find restaurant by ID", description = "Fetches a restaurant by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Restaurant found"),
                    @ApiResponse(responseCode = "404", description = "Restaurant not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Restaurant>> findRestaurantById(@Parameter(description = "Restaurant Id") @PathVariable Long id) {
        LOGGER.info("GET -> /api/restaurants/{} - Fetching restaurant by ID", id);
        Optional<Restaurant> restaurant = restaurantService.findUserById(id);
        if (restaurant.isPresent()) {
            return ResponseEntity.ok(restaurant);
        } else {
            LOGGER.info("GET -> /api/restaurants/{} - Not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Save a new restaurant", description = "Saves a new restaurant to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Restaurant saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping
    public ResponseEntity<Void> saveRestaurant(@RequestBody RestaurantDTO restaurant) {
        try {
            restaurantService.saveRestaurant(restaurant);
            LOGGER.info("POST -> /api/restaurants - Saving restaurant: {}", restaurant);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            LOGGER.error("POST -> /api/restaurants - Error saving restaurant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Update restaurant by ID", description = "Updates a restaurant's information by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Restaurant updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Restaurant not found")
            })
    @PutMapping
    public ResponseEntity<Void> updateRestaurant(@RequestBody RestaurantDTO restaurant) {
        try{
            restaurantService.updateRestaurant(restaurant);
            LOGGER.info("PUT -> /api/restaurants - Updating restaurant with ID: {}", restaurant.id());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.error("PUT -> /api/restaurants - Error updating restaurant: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete restaurant by ID", description = "Deletes a restaurant by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Restaurant not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@Parameter(description = "Restaurant Id") @PathVariable Long id) {
        try{
            restaurantService.deleteRestaurant(id);
            LOGGER.info("DELETE -> /api/restaurants/{} - Deleting restaurant by ID", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.error("DELETE -> /api/restaurants/{} - Error deleting restaurant: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
