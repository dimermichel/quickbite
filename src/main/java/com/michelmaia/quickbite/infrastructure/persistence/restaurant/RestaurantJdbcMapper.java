package com.michelmaia.quickbite.infrastructure.persistence.restaurant;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.restaurant.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Maps JDBC ResultSet to Domain Entity
 */
@Component
public class RestaurantJdbcMapper {

    public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = null;
        if (rs.getString("street") != null) {
            address = new Address(
                    rs.getString("street"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zip_code")
            );
        }

        return Restaurant.reconstruct(
                rs.getLong("id"),
                rs.getLong("owner_id"),
                rs.getString("name"),
                rs.getString("cuisine"),
                address,
                rs.getString("opening_hours"),
                rs.getDouble("rating"),
                rs.getBoolean("is_open"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}