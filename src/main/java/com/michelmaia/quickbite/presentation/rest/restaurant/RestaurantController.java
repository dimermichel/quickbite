
package com.michelmaia.quickbite.presentation.rest.restaurant;

import com.michelmaia.quickbite.application.dto.PageResponseDTO;
import com.michelmaia.quickbite.application.usecase.restaurant.*;
import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.CreateRestaurantRequest;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.RestaurantResponse;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.UpdateRestaurantRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Presentation Layer: Restaurant Controller
 */
@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", description = "Endpoints for managing restaurants")
public class RestaurantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetRestaurantUseCase getRestaurantUseCase;
    private final ListRestaurantsUseCase listRestaurantsUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;

    public RestaurantController(
            CreateRestaurantUseCase createRestaurantUseCase,
            GetRestaurantUseCase getRestaurantUseCase,
            ListRestaurantsUseCase listRestaurantsUseCase,
            UpdateRestaurantUseCase updateRestaurantUseCase,
            DeleteRestaurantUseCase deleteRestaurantUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.getRestaurantUseCase = getRestaurantUseCase;
        this.listRestaurantsUseCase = listRestaurantsUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Create a new restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Owner or Admin role required")
    })
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {
        LOGGER.info("POST -> /api/restaurants - Creating restaurant: {}", request.name());

        var command = new CreateRestaurantUseCase.CreateRestaurantCommand(
                request.ownerId(),
                request.name(),
                request.cuisine(),
                request.address().street(),
                request.address().city(),
                request.address().state(),
                request.address().zipCode(),
                request.openingHours(),
                request.rating(),
                request.isOpen()
        );

        Restaurant restaurant = createRestaurantUseCase.execute(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(RestaurantResponse.fromDomain(restaurant));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "Get restaurant by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @Parameter(description = "Restaurant ID") @PathVariable Long id) {
        LOGGER.info("GET -> /api/restaurants/{} - Fetching restaurant", id);

        Restaurant restaurant = getRestaurantUseCase.execute(id);

        return ResponseEntity.ok(RestaurantResponse.fromDomain(restaurant));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "List all restaurants with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    public ResponseEntity<PageResponseDTO<RestaurantResponse>> listRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating) {
        LOGGER.info("GET -> /api/restaurants - Listing restaurants (page={}, size={}, cuisine={}, minRating={})",
                page, size, cuisine, minRating);

        var query = new ListRestaurantsUseCase.ListRestaurantsQuery(
                page, size, cuisine, minRating
        );

        PageResponseDTO<Restaurant> restaurantsPage = listRestaurantsUseCase.execute(query);

        // Convert to DTOs
        List<RestaurantResponse> responses = restaurantsPage.getData().stream()
                .map(RestaurantResponse::fromDomain)
                .toList();

        PageResponseDTO<RestaurantResponse> response = new PageResponseDTO<>(
                responses,
                restaurantsPage.getPage(),
                restaurantsPage.getSize(),
                restaurantsPage.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-cuisine")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "Find restaurants by cuisine")
    public ResponseEntity<PageResponseDTO<RestaurantResponse>> findByCuisine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String cuisine) {
        return listRestaurants(page, size, cuisine, null);
    }

    @GetMapping("/by-rating")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "Find restaurants by minimum rating")
    public ResponseEntity<PageResponseDTO<RestaurantResponse>> findByRating(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Double minRating) {
        return listRestaurants(page, size, null, minRating);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Update restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        LOGGER.info("PUT -> /api/restaurants/{} - Updating restaurant", id);

        var command = new UpdateRestaurantUseCase.UpdateRestaurantCommand(
                id,
                request.name(),
                request.cuisine(),
                request.address() != null ? request.address().street() : null,
                request.address() != null ? request.address().city() : null,
                request.address() != null ? request.address().state() : null,
                request.address() != null ? request.address().zipCode() : null,
                request.openingHours(),
                request.rating(),
                request.isOpen()
        );

        Restaurant restaurant = updateRestaurantUseCase.execute(command);

        return ResponseEntity.ok(RestaurantResponse.fromDomain(restaurant));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Delete restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        LOGGER.info("DELETE -> /api/restaurants/{} - Deleting restaurant", id);

        deleteRestaurantUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}