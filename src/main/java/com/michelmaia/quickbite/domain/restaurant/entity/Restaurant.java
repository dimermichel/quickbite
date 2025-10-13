
package com.michelmaia.quickbite.domain.restaurant.entity;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.restaurant.exception.InvalidRestaurantDataException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Entity - Pure business logic, no framework dependencies
 * Represents a Restaurant in the business domain
 */
public class Restaurant {

    private final Long id;
    private final Long ownerId;
    private String name;
    private String cuisine;
    private Address address;
    private String openingHours;
    private Double rating;
    private boolean isOpen;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Private constructor - use factory methods
    private Restaurant(Long id, Long ownerId, String name, String cuisine,
                       Address address, String openingHours, Double rating,
                       boolean isOpen, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.cuisine = cuisine;
        this.address = address;
        this.openingHours = openingHours;
        this.rating = rating;
        this.isOpen = isOpen;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        validate();
    }

    // Factory method for creating a new restaurant
    public static Restaurant createNew(Long ownerId, String name, String cuisine,
                                       Address address, String openingHours) {
        return new Restaurant(null, ownerId, name, cuisine, address, openingHours,
                0.0, true, null, null);
    }

    // Factory method for reconstructing from database
    public static Restaurant reconstruct(Long id, Long ownerId, String name, String cuisine,
                                         Address address, String openingHours, Double rating,
                                         boolean isOpen, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Restaurant(id, ownerId, name, cuisine, address, openingHours,
                rating, isOpen, createdAt, updatedAt);
    }

    // Business rules validation
    private void validate() {
        if (ownerId == null) {
            throw new InvalidRestaurantDataException("Owner ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRestaurantDataException("Restaurant name cannot be empty");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new InvalidRestaurantDataException("Restaurant name must be between 2 and 100 characters");
        }
        if (cuisine == null || cuisine.trim().isEmpty()) {
            throw new InvalidRestaurantDataException("Cuisine type cannot be empty");
        }
        if (address == null) {
            throw new InvalidRestaurantDataException("Address cannot be null");
        }
        if (rating != null && (rating < 0.0 || rating > 5.0)) {
            throw new InvalidRestaurantDataException("Rating must be between 0.0 and 5.0");
        }
    }

    // Business methods
    public void updateInfo(String name, String cuisine, Address address, String openingHours) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (cuisine != null && !cuisine.trim().isEmpty()) {
            this.cuisine = cuisine;
        }
        if (address != null) {
            this.address = address;
        }
        if (openingHours != null) {
            this.openingHours = openingHours;
        }
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void updateRating(Double newRating) {
        if (newRating != null && newRating >= 0.0 && newRating <= 5.0) {
            this.rating = newRating;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new InvalidRestaurantDataException("Rating must be between 0.0 and 5.0");
        }
    }

    public void open() {
        this.isOpen = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        this.isOpen = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isNew() {
        return this.id == null;
    }

    public boolean isOwnedBy(Long userId) {
        return this.ownerId.equals(userId);
    }

    // Getters
    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public Address getAddress() { return address; }
    public String getOpeningHours() { return openingHours; }
    public Double getRating() { return rating; }
    public boolean isOpen() { return isOpen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", rating=" + rating +
                ", isOpen=" + isOpen +
                '}';
    }
}