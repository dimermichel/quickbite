package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.dto.MenuItemDTO;
import com.michelmaia.quickbite.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Tag(name = "Menu Items Management", description = "Endpoints for managing menu items")
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuItemController.class);
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @Operation(summary = "Find menu item by ID", description = "Fetches a menu item by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu item found"),
                    @ApiResponse(responseCode = "404", description = "Menu item not found")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Optional<MenuItemDTO>> findMenuItemById(@Parameter(description = "Menu Item Id") @PathVariable Long id) {
        LOGGER.info("GET -> /api/menu-items/{} - Fetching menu item by id", id);
        Optional<MenuItemDTO> menuItem = menuItemService.findMenuItemById(id);
        if (menuItem.isPresent()) {
            return ResponseEntity.ok(menuItem);
        } else {
            LOGGER.info("GET -> /api/menu-items/{} - Menu item not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Find a list of menu items by name and restaurant", description = "Fetches a list of menu items by their name and restaurant ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu items found"),
                    @ApiResponse(responseCode = "404", description = "Menu item not found")
            })
    @GetMapping("/restaurant/search")
    public ResponseEntity<List<MenuItemDTO>> findByNameByRestaurant(@Parameter(description = "Menu Item Name for search") @RequestParam String name,
                                                                    @Parameter(description = "Restaurant Id") @RequestParam Long restaurantId) {
        LOGGER.info("GET -> /api/menu-items/restaurant/search - Fetching menu item by name: {} and restaurant id: {}", name, restaurantId);
        var nameDecoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        List<MenuItemDTO> menuItems = menuItemService.findByNameByRestaurant(nameDecoded, restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @Operation(summary = "Find all available menu items by restaurant", description = "Fetches a list of all available menu items for a specific restaurant",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of available menu items retrieved successfully")
            })
    @GetMapping("/restaurant/available")
    public ResponseEntity<List<MenuItemDTO>> findAllAvailableByRestaurant(@RequestParam Boolean available, @RequestParam Long restaurantId) {
        LOGGER.info("GET -> /api/menu-items/restaurant/available - Fetching all available menu items for restaurant id: {}", restaurantId);
        List<MenuItemDTO> menuItems = menuItemService.findAllAvailableByRestaurant(available, restaurantId);
        return ResponseEntity.ok(menuItems);
    }

    @Operation(summary = "Find all menu items by restaurant", description = "Fetches a list of all menu items for a specific restaurant",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of menu items retrieved successfully")
            })
    @GetMapping("/restaurant")
    public ResponseEntity<List<MenuItemDTO>> findAllByRestaurant(@RequestParam Long restaurantId) {
        LOGGER.info("GET -> /api/menu-items/restaurant - Fetching all menu items for restaurant id: {}", restaurantId);
        List<MenuItemDTO> menuItems = menuItemService.findAllByRestaurant(restaurantId);
        return ResponseEntity.ok(menuItems);
    }


    @Operation(summary = "Save a new menu item", description = "Saves a new menu item to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Menu item saved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping
    public ResponseEntity<?> saveMenuItem(@RequestBody MenuItemDTO menuItem) {
        LOGGER.info("POST -> /api/menu-items - Saving menu item: {}", menuItem);
        try {
            menuItemService.saveMenuItem(menuItem);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            LOGGER.error("POST -> /api/menu-items - Error saving menu item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update menu item by ID", description = "Updates a menu item's information by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Menu item updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Menu item not found")
            })
    @PutMapping
    public ResponseEntity<?> updateMenuItem(@RequestBody MenuItemDTO menuItem) {
        LOGGER.info("PUT -> /api/menu-items - Updating menu item: {}", menuItem);
        try {
            menuItemService.updateMenuItem(menuItem);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            LOGGER.error("PUT -> /api/menu-items - Error updating menu item: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            LOGGER.error("PUT -> /api/menu-items - Error updating menu item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(summary = "Delete menu item by ID", description = "Deletes a menu item by their ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Menu item not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@Parameter(description = "Menu Item Id") @PathVariable Long id) {
        try {
            menuItemService.deleteMenuItem(id);
            LOGGER.info("DELETE -> /api/menu-items/{} - Deleting menu item by ID", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            LOGGER.error("DELETE -> /api/menu-items/{} - Error deleting menu item: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
