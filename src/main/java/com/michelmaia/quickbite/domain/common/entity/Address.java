package com.michelmaia.quickbite.domain.common.entity;

import java.util.Objects;

/**
 * Domain Value Object for Address
 */
public class Address {
    
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    
    public Address(String street, String city, String state, String zipCode) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be empty");
        }
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Zip code cannot be empty");
        }
        
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
    
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state) &&
               Objects.equals(zipCode, address.zipCode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zipCode);
    }
    
    @Override
    public String toString() {
        return street + ", " + city + ", " + state + " " + zipCode;
    }
}
