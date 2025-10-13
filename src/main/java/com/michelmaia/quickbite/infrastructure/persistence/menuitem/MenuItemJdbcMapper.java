package com.michelmaia.quickbite.infrastructure.persistence.menuitem;

import com.michelmaia.quickbite.domain.menuitem.entity.MenuItem;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Maps JDBC ResultSet to Domain Entity
 */
@Component
public class MenuItemJdbcMapper {

    public MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MenuItem.reconstruct(
                rs.getLong("id"),
                rs.getLong("restaurant_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getString("image_url"),
                rs.getBoolean("is_available"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}