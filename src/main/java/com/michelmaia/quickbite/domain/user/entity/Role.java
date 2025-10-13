package com.michelmaia.quickbite.domain.user.entity;

/**
 * Domain Role - Simple enum for user roles
 */
public enum Role {
    USER(1L, "USER"),
    OWNER(2L, "OWNER"),
    ADMIN(3L, "ADMIN");
    
    private final Long id;
    private final String name;
    
    Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public static Role fromId(Long id) {
        for (Role role : values()) {
            if (role.id.equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role id: " + id);
    }
    
    public static Role fromName(String name) {
        for (Role role : values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role name: " + name);
    }
}
