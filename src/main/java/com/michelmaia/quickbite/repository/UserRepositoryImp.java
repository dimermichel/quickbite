package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.mapper.UserRowMapper;
import com.michelmaia.quickbite.model.Role;
import com.michelmaia.quickbite.model.User;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepositoryImp implements UserRepository {

    private final JdbcClient jdbcClient;
    private final UserRowMapper userRowMapper;

    public UserRepositoryImp(JdbcClient jdbcClient, UserRowMapper userRowMapper) {
        this.jdbcClient = jdbcClient;
        this.userRowMapper = userRowMapper;
    }

    private List<Role> findRolesByUserId(Long userId) {
        return jdbcClient.sql("""
                            SELECT r.id, r.name
                            FROM roles r
                            JOIN user_roles ur ON ur.role_id = r.id
                            WHERE ur.user_id = :userId
                        """)
                .param("userId", userId)
                .query((rs, rowNum) -> new Role(rs.getLong("id"), rs.getString("name")))
                .list();
    }

    private void insertUserRoles(Long userId, List<Role> roles) {
        for (Role role : roles) {
            jdbcClient.sql("""
                                INSERT INTO user_roles (user_id, role_id)
                                VALUES (:userId, :roleId)
                            """)
                    .param("userId", userId)
                    .param("roleId", role.getId())
                    .update();
        }
    }

    private void updateUserRoles(Long userId, List<Role> roles) {
        jdbcClient.sql("DELETE FROM user_roles WHERE user_id = :userId")
                .param("userId", userId)
                .update();
        insertUserRoles(userId, roles);
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled, u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            WHERE u.id = :id
                        """)
                .param("id", id)
                .query(userRowMapper)
                .optional();
        // If a user is found, fetch and set roles
        userOpt.ifPresent(user -> user.setRoles(findRolesByUserId(id)));
        return userOpt;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> user = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled, u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            WHERE u.username = :username
                        """)
                .param("username", username)
                .query(userRowMapper)
                .optional();

        // If a user is found, fetch and set roles
        user.ifPresent(u -> u.setRoles(findRolesByUserId(u.getId())));
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return jdbcClient.sql("SELECT COUNT(*) FROM users WHERE username = :username")
                .param("username", username)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public PageResponseDTO<User> findAllPaginated(Pageable pageable, Optional<Long> roleId) {
        // Fetch total count based on roleId
        Long total;
        if (roleId.isPresent()) {
            total = jdbcClient.sql("""
                            SELECT COUNT(DISTINCT u.id) 
                            FROM users u 
                            JOIN user_roles ur ON u.id = ur.user_id 
                            WHERE ur.role_id = :roleId
                            """)
                    .param("roleId", roleId.get())
                    .query(Long.class)
                    .single();
        } else {
            total = jdbcClient.sql("SELECT COUNT(*) FROM users")
                    .query(Long.class)
                    .single();
        }

        String sortClause = buildSortClause(pageable);
        String sql = getSqlString(roleId, sortClause);

        var query = jdbcClient.sql(sql)
                .param("size", pageable.getPageSize())
                .param("offset", pageable.getOffset());

        if (roleId.isPresent()) {
            query = query.param("roleId", roleId.get());
        }

        List<User> users = query.query(userRowMapper).list();
        users.forEach(user -> user.setRoles(findRolesByUserId(user.getId())));
        return new PageResponseDTO<>(
                users,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                total
        );

    }

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "id", "name", "email", "username", "enabled", "created_at", "updated_at"
    );

    private String buildSortClause(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return "";
        }

        StringBuilder sortBuilder = new StringBuilder(" ORDER BY ");
        pageable.getSort().forEach(order -> {
            String rawProperty = order.getProperty();
            String property = rawProperty.replaceAll("[\\[\\]\"']", "").trim();

            // Validate the property against whitelist
            if (!isValidSortProperty(property)) {
                throw new IllegalArgumentException("Invalid sort property: " + property);
            }

            sortBuilder.append("u.").append(order.getProperty())
                    .append(" ").append(order.getDirection().name())
                    .append(", ");
        });

        // Remove trailing comma and space
        String result = sortBuilder.toString();
        return result.substring(0, result.length() - 2);
    }

    private boolean isValidSortProperty(String property) {
        // Check if a property is in the whitelist
        if (!ALLOWED_SORT_COLUMNS.contains(property)) {
            return false;
        }
        // Additional validation: only alphanumeric and underscore
        return property.matches("^[a-zA-Z0-9_]+$");
    }

    private static String getSqlString(Optional<Long> roleId, String sortClause) {
        // Ensure sortClause is properly formatted for SQL
        String formattedSortClause = "";
        if (sortClause != null && !sortClause.trim().isEmpty()) {
            // Remove array brackets if present and format properly
            String cleanSortClause = sortClause.replaceAll("[\\[\\]]", "").trim();
            formattedSortClause = cleanSortClause;
        }

        // Build the SQL query based on the presence of roleId
        String sql = "";
        if (roleId.isPresent()) {
            sql = """
                    SELECT u.id, u.name, u.email, u.username, u.password, u.enabled, u.created_at, u.updated_at,
                           a.street, a.city, a.state, a.zip_code
                    FROM users u
                    LEFT JOIN addresses a ON u.address_id = a.id
                    JOIN user_roles ur ON u.id = ur.user_id
                    WHERE ur.role_id = :roleId"""
                    + " " + formattedSortClause + """
                        LIMIT :size OFFSET :offset
                    """;
        } else {
            sql = """
                    SELECT u.id, u.name, u.email, u.username, u.password, u.enabled, u.created_at, u.updated_at,
                           a.street, a.city, a.state, a.zip_code
                    FROM users u
                    LEFT JOIN addresses a ON u.address_id = a.id""" +
                    " " + formattedSortClause + """
                        LIMIT :size OFFSET :offset
                    """;
        }
        return sql;
    }

    @Override
    public User save(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        // Insert the address first (if any)
        Long addressId = null;
        if (user.getAddress() != null) {
            addressId = jdbcClient.sql("""
                                INSERT INTO addresses (street, city, state, zip_code)
                                VALUES (:street, :city, :state, :zipCode)
                                RETURNING id
                            """)
                    .param("street", user.getAddress().getStreet())
                    .param("city", user.getAddress().getCity())
                    .param("state", user.getAddress().getState())
                    .param("zipCode", user.getAddress().getZipCode())
                    .query(Long.class)
                    .single();
        }

        Long userId = jdbcClient.sql("""
                            INSERT INTO users (name, username, password, email, address_id, enabled)
                            VALUES (:name, :username, :password, :email, :addressId, :enabled)
                            RETURNING id
                        """)
                .param("name", user.getName())
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("email", user.getEmail())
                .param("addressId", addressId)
                .param("enabled", user.getEnabled())
                .query(Long.class)
                .single();

        insertUserRoles(userId, user.getRoles());
        return findById(userId).orElseThrow();
    }

    @Override
    public User update(User user, Long id) {
        // Handle address update/creation
        if (user.getAddress() != null) {
            // First, check if the user already has an address
            Optional<Long> existingAddressId = jdbcClient.sql(
                            "SELECT address_id FROM users WHERE id = :userId")
                    .param("userId", id)
                    .query(Long.class)
                    .optional();

            if (existingAddressId.isPresent() && existingAddressId.get() != null) {
                // Update existing address
                jdbcClient.sql("""
                                    UPDATE addresses
                                    SET street = :street, city = :city, state = :state, zip_code = :zipCode
                                    WHERE id = :addressId
                                """)
                        .param("street", user.getAddress().getStreet())
                        .param("city", user.getAddress().getCity())
                        .param("state", user.getAddress().getState())
                        .param("zipCode", user.getAddress().getZipCode())
                        .param("addressId", existingAddressId.get())
                        .update();
            } else {
                // Create a new address and link it to the user
                Long newAddressId = jdbcClient.sql("""
                                    INSERT INTO addresses (street, city, state, zip_code)
                                    VALUES (:street, :city, :state, :zipCode)
                                    RETURNING id
                                """)
                        .param("street", user.getAddress().getStreet())
                        .param("city", user.getAddress().getCity())
                        .param("state", user.getAddress().getState())
                        .param("zipCode", user.getAddress().getZipCode())
                        .query(Long.class)
                        .single();

                // Update the user to reference the new address
                jdbcClient.sql("UPDATE users SET address_id = :addressId WHERE id = :userId")
                        .param("addressId", newAddressId)
                        .param("userId", id)
                        .update();
            }
        }

        // Update all user fields including password if provided
        jdbcClient.sql("""
                    UPDATE users
                    SET name = :name,
                        username = :username,
                        email = :email,
                        password = :password,
                        enabled = :enabled,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE id = :id
                """)
                .param("name", user.getName())
                .param("username", user.getUsername())
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("enabled", user.getEnabled())
                .param("id", id)
                .update();

        updateUserRoles(id, user.getRoles());
        return findById(id).orElseThrow();
    }


    @Override
    public Integer deleteById(Long id) {
        // Check if user owns any restaurants
        Long restaurantCount = jdbcClient.sql("SELECT COUNT(*) FROM restaurants WHERE owner_id = :userId")
                .param("userId", id)
                .query(Long.class)
                .single();
        
        if (restaurantCount > 0) {
            throw new IllegalStateException("Cannot delete user. User owns " + restaurantCount + " restaurant(s)");
        }

        Optional<Long> addressIdOpt = jdbcClient.sql("SELECT address_id FROM users WHERE id = :id")
                .param("id", id)
                .query(Long.class)
                .optional();

        int rows = jdbcClient.sql("DELETE FROM users WHERE id = :id")
                .param("id", id)
                .update();

        addressIdOpt.ifPresent(addressId -> {
            if (addressId != null) {
                jdbcClient.sql("DELETE FROM addresses WHERE id = :addressId")
                        .param("addressId", addressId)
                        .update();
            }
        });

        return rows;

    }
}
