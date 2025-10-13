package com.michelmaia.quickbite.infrastructure.persistence.user;

import com.michelmaia.quickbite.domain.common.entity.Address;
import com.michelmaia.quickbite.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Maps JDBC ResultSet to Domain Entity
 */
@Component
public class UserJdbcMapper {
    
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Address address = null;
        if (rs.getString("street") != null) {
            address = new Address(
                rs.getString("street"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("zip_code")
            );
        }
        
        return User.reconstruct(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("password"),
            address,
            new ArrayList<>(), // Roles will be fetched separately
            rs.getBoolean("enabled"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}
