package com.michelmaia.quickbite.presentation.rest.menuitem;

import com.michelmaia.quickbite.application.usecase.menuitem.*;
import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.CreateMenuItemRequest;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.MenuItemResponse;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.UpdateMenuItemRequest;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Presentation Layer: Menu Item Controller
 */
@RestController
@RequestMapping("/api/menu-items")
@Tag(name = "Menu Items Management", description = "Endpoints for managing menu items")
public class MenuItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemController.class);

    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final GetMenuItemUseCase getMenuItemUseCase;
    private final ListMenuItemsUseCase listMenuItemsUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;

    public MenuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            GetMenuItemUseCase getMenuItemUseCase,
            ListMenuItemsUseCase listMenuItemsUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase) {
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.getMenuItemUseCase = getMenuItemUseCase;
        this.listMenuItemsUseCase = listMenuItemsUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Create a new menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Owner or Admin role required"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequest request) {
        LOGGER.info("POST -> /api/menu-items - Creating menu item: {}", request.name());

        var command = new CreateMenuItemUseCase.CreateMenuItemCommand(
                request.restaurantId(),
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl(),
                request.isAvailable()
        );

        MenuItem menuItem = createMenuItemUseCase.execute(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MenuItemResponse.fromDomain(menuItem));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "Get menu item by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item found"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @Parameter(description = "Menu Item ID") @PathVariable Long id) {
        LOGGER.info("GET -> /api/menu-items/{} - Fetching menu item", id);

        MenuItem menuItem = getMenuItemUseCase.execute(id);

        return ResponseEntity.ok(MenuItemResponse.fromDomain(menuItem));
    }

    @GetMapping("/restaurant")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "List all menu items for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    })
    public ResponseEntity<List<MenuItemResponse>> listMenuItemsByRestaurant(
            @Parameter(description = "Restaurant ID") @RequestParam Long restaurantId) {
        LOGGER.info("GET -> /api/menu-items/restaurant - Listing menu items for restaurant {}", restaurantId);

        var query = new ListMenuItemsUseCase.ListMenuItemsQuery(restaurantId);
        List<MenuItem> menuItems = listMenuItemsUseCase.execute(query);

        List<MenuItemResponse> responses = menuItems.stream()
                .map(MenuItemResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/restaurant/available")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "List available/unavailable menu items for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    })
    public ResponseEntity<List<MenuItemResponse>> listMenuItemsByAvailability(
            @Parameter(description = "Restaurant ID") @RequestParam Long restaurantId,
            @Parameter(description = "Availability status") @RequestParam Boolean available) {
        LOGGER.info("GET -> /api/menu-items/restaurant/available - Listing {} menu items for restaurant {}",
                available ? "available" : "unavailable", restaurantId);

        var query = new ListMenuItemsUseCase.ListMenuItemsQuery(restaurantId, available);
        List<MenuItem> menuItems = listMenuItemsUseCase.execute(query);

        List<MenuItemResponse> responses = menuItems.stream()
                .map(MenuItemResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/restaurant/search")
    @PreAuthorize("hasAnyRole('USER', 'OWNER', 'ADMIN')")
    @Operation(summary = "Search menu items by name for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu items found")
    })
    public ResponseEntity<List<MenuItemResponse>> searchMenuItemsByName(
            @Parameter(description = "Restaurant ID") @RequestParam Long restaurantId,
            @Parameter(description = "Name to search") @RequestParam String name) {
        LOGGER.info("GET -> /api/menu-items/restaurant/search - Searching menu items by name '{}' for restaurant {}",
                name, restaurantId);

        String nameDecoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        var query = new ListMenuItemsUseCase.ListMenuItemsQuery(restaurantId, null, nameDecoded);
        List<MenuItem> menuItems = listMenuItemsUseCase.execute(query);

        List<MenuItemResponse> responses = menuItems.stream()
                .map(MenuItemResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Update menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        LOGGER.info("PUT -> /api/menu-items/{} - Updating menu item", id);

        var command = new UpdateMenuItemUseCase.UpdateMenuItemCommand(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.imageUrl(),
                request.isAvailable()
        );

        MenuItem menuItem = updateMenuItemUseCase.execute(command);

        return ResponseEntity.ok(MenuItemResponse.fromDomain(menuItem));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @Operation(summary = "Delete menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        LOGGER.info("DELETE -> /api/menu-items/{} - Deleting menu item", id);

        deleteMenuItemUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}