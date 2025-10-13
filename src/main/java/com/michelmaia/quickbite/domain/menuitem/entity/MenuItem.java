package com.michelmaia.quickbite.domain.menuitem.entity;

import com.michelmaia.quickbite.domain.menuitem.exception.InvalidMenuItemDataException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Entity - Pure business logic, no framework dependencies
 * Represents a MenuItem in the business domain
 */
public class MenuItem {

    private final Long id;
    private final Long restaurantId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private boolean isAvailable;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Private constructor - use factory methods
    private MenuItem(Long id, Long restaurantId, String name, String description,
                     Double price, String imageUrl, boolean isAvailable,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validate();
    }

    // Factory method for creating a new menu item
    public static MenuItem createNew(Long restaurantId, String name, String description,
                                     Double price, String imageUrl) {
        return new MenuItem(null, restaurantId, name, description, price,
                imageUrl, true, null, null);
    }

    // Factory method for reconstructing from database
    public static MenuItem reconstruct(Long id, Long restaurantId, String name,
                                       String description, Double price, String imageUrl,
                                       boolean isAvailable, LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {
        return new MenuItem(id, restaurantId, name, description, price, imageUrl,
                isAvailable, createdAt, updatedAt);
    }

    // Business rules validation
    private void validate() {
        if (restaurantId == null) {
            throw new InvalidMenuItemDataException("Restaurant ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidMenuItemDataException("Menu item name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new InvalidMenuItemDataException("Name must be between 2 and 100 characters");
        }
        if (price == null || price < 0) {
            throw new InvalidMenuItemDataException("Price must be greater than or equal to 0");
        }
        if (price > 999999.99) {
            throw new InvalidMenuItemDataException("Price cannot exceed 999999.99");
        }
    }

    // Business methods
    public void updateInfo(String name, String description, Double price, String imageUrl) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null && price >= 0) {
            this.price = price;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updatePrice(Double newPrice) {
        if (newPrice == null || newPrice < 0) {
            throw new InvalidMenuItemDataException("Price must be greater than or equal to 0");
        }
        if (newPrice > 999999.99) {
            throw new InvalidMenuItemDataException("Price cannot exceed 999999.99");
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAvailable() {
        this.isAvailable = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUnavailable() {
        this.isAvailable = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isNew() {
        return this.id == null;
    }

    public boolean belongsToRestaurant(Long restaurantId) {
        return this.restaurantId.equals(restaurantId);
    }

    // Getters
    public Long getId() { return id; }
    public Long getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isAvailable() { return isAvailable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                '}';
    }
}