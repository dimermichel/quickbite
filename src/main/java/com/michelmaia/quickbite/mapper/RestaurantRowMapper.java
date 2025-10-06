package com.michelmaia.quickbite.mapper;

import com.michelmaia.quickbite.model.Address;
import com.michelmaia.quickbite.model.Restaurant;
import com.michelmaia.quickbite.model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RestaurantRowMapper implements RowMapper<Restaurant> {
    @Override
    public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = null;
        if (rs.getString("street") != null) {
            address = new Address();
            address.setId(rs.getLong("address_id"));
            address.setStreet(rs.getString("street"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setZipCode(rs.getString("zip_code"));
        }

        User user = null;
        Long ownerId = rs.getObject("owner_id", Long.class);
        if (ownerId != null) {
            user = new User();
            user.setId(ownerId);
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getLong("id"));
        restaurant.setName(rs.getString("name"));
        restaurant.setCuisine(rs.getString("cuisine"));
        restaurant.setRating(rs.getDouble("rating"));
        restaurant.setOpeningHours(rs.getString("opening_hours"));
        restaurant.setIsOpen(rs.getBoolean("is_open"));
        restaurant.setOwner(user);
        restaurant.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        restaurant.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        restaurant.setAddress(address);

        // Transient field for pagination - only set if column exists
        try {
            restaurant.setTotalCount(rs.getLong("total_count"));
        } catch (SQLException e) {
            // Column doesn't exist, leave totalCount as null or default value
            restaurant.setTotalCount(null);
        }
        
        return restaurant;
    }
}
