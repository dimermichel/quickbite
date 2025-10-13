package com.michelmaia.quickbite.domain.user.entity;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.user.exception.InvalidUserDataException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain Entity - Pure business logic, no framework dependencies
 * This represents a User in the business domain
 */
public class User {
    
    private final Long id;
    private String name;
    private String email;
    private String username;
    private String password; // Will be hashed by infrastructure
    private Address address;
    private List<Role> roles;
    private boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor - use factory methods to create instances
    private User(Long id, String name, String email, String username, 
                 String password, Address address, List<Role> roles, 
                 boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.address = address;
        this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        
        validate();
    }
    
    // Factory method for creating a new user (registration)
    public static User createNew(String name, String email, String username,
                                 String password, Address address) {
        return new User(null, name, email, username, password, address,
                List.of(Role.USER), true, null, null);
    }
    
    // Factory method for reconstructing from database
    public static User reconstruct(Long id, String name, String email, String username, 
                                   String password, Address address, List<Role> roles, 
                                   boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, name, email, username, password, address, roles, 
                       enabled, createdAt, updatedAt);
    }
    
    // Business rules validation
    private void validate() {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidUserDataException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new InvalidUserDataException("Username must be between 3 and 50 characters");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidUserDataException("Invalid email format");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("Name cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidUserDataException("Password must be at least 4 characters");
        }
    }
    
    // Business methods
    public void updateProfile(String name, String email, Address address) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            this.email = email;
        }
        if (address != null) {
            this.address = address;
        }
        this.updatedAt = LocalDateTime.now();
        validate();
    }
    
    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new InvalidUserDataException("Password must be at least 4 characters");
        }
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addRole(Role role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
    
    public boolean isNew() {
        return this.id == null;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Address getAddress() { return address; }
    public List<Role> getRoles() { return new ArrayList<>(roles); }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}