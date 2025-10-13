package com.michelmaia.quickbite.infrastructure.persistence.user;

import com.michelmaia.quickbite.domain.user.entity.Role;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.exception.UserHasRestaurantsException;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure Adapter: Implements domain repository using JDBC
 * This is your existing UserRepositoryImp adapted to Clean Architecture
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JdbcClient jdbcClient;
    private final UserJdbcMapper mapper;

    public UserRepositoryAdapter(JdbcClient jdbcClient, UserJdbcMapper mapper) {
        this.jdbcClient = jdbcClient;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        if (user.isNew()) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        // Insert address first
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

        // Insert user
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
                .param("enabled", user.isEnabled())
                .query(Long.class)
                .single();

        // Insert roles
        insertUserRoles(userId, user.getRoles());

        return findById(userId).orElseThrow();
    }

    private User update(User user) {
        // Update address if exists
        if (user.getAddress() != null) {
            Optional<Long> addressIdOpt = jdbcClient.sql(
                            "SELECT address_id FROM users WHERE id = :userId")
                    .param("userId", user.getId())
                    .query(Long.class)
                    .optional();

            if (addressIdOpt.isPresent() && addressIdOpt.get() != null) {
                jdbcClient.sql("""
                                    UPDATE addresses
                                    SET street = :street, city = :city, state = :state, zip_code = :zipCode
                                    WHERE id = :addressId
                                """)
                        .param("street", user.getAddress().getStreet())
                        .param("city", user.getAddress().getCity())
                        .param("state", user.getAddress().getState())
                        .param("zipCode", user.getAddress().getZipCode())
                        .param("addressId", addressIdOpt.get())
                        .update();
            }
        }

        // Update user
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
                .param("enabled", user.isEnabled())
                .param("id", user.getId())
                .update();

        // Update roles
        updateUserRoles(user.getId(), user.getRoles());

        return findById(user.getId()).orElseThrow();
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled, 
                                   u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            WHERE u.id = :id
                        """)
                .param("id", id)
                .query(mapper::mapRow)
                .optional();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Role> roles = findRolesByUserId(user.getId());
            // Reconstruct with roles
            User userWithRoles = User.reconstruct(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getAddress(),
                    roles,
                    user.isEnabled(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
            return Optional.of(userWithRoles);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                    SELECT u.id, u.name, u.email, u.username, u.password, u.enabled,
                           u.created_at, u.updated_at,
                           a.id as address_id, a.street, a.city, a.state, a.zip_code
                    FROM users u
                    LEFT JOIN addresses a ON u.address_id = a.id
                    WHERE u.username = ?
                """;

        try {
            User user = jdbcClient.sql(sql)
                    .param(username)
                    .query(mapper::mapRow)
                    .single();

            // IMPORTANT: Load roles for the user
            List<Role> roles = findRolesByUserId(user.getId());

            // Reconstruct user with roles
            return Optional.of(User.reconstruct(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getAddress(),
                    roles,  // Make sure roles are included
                    user.isEnabled(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> userOpt = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled,
                                   u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            WHERE u.email = :email
                        """)
                .param("email", email)
                .query(mapper::mapRow)
                .optional();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Role> roles = findRolesByUserId(user.getId());
            // Reconstruct with roles
            User userWithRoles = User.reconstruct(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getAddress(),
                    roles,
                    user.isEnabled(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
            return Optional.of(userWithRoles);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll(int page, int size) {
        int offset = page * size;
        List<User> users = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled,
                                   u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            LIMIT :size OFFSET :offset
                        """)
                .param("size", size)
                .param("offset", offset)
                .query(mapper::mapRow)
                .list();

        // Load roles for each user
        return users.stream()
                .map(user -> {
                    List<Role> roles = findRolesByUserId(user.getId());
                    return User.reconstruct(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getUsername(),
                            user.getPassword(),
                            user.getAddress(),
                            roles,
                            user.isEnabled(),
                            user.getCreatedAt(),
                            user.getUpdatedAt()
                    );
                })
                .toList();
    }

    @Override
    public List<User> findByRole(Long roleId, int page, int size) {
        int offset = page * size;
        List<User> users = jdbcClient.sql("""
                            SELECT u.id, u.name, u.email, u.username, u.password, u.enabled,
                                   u.created_at, u.updated_at,
                                   a.street, a.city, a.state, a.zip_code
                            FROM users u
                            LEFT JOIN addresses a ON u.address_id = a.id
                            JOIN user_roles ur ON u.id = ur.user_id
                            WHERE ur.role_id = :roleId
                            LIMIT :size OFFSET :offset
                        """)
                .param("roleId", roleId)
                .param("size", size)
                .param("offset", offset)
                .query(mapper::mapRow)
                .list();

        // Load roles for each user
        return users.stream()
                .map(user -> {
                    List<Role> roles = findRolesByUserId(user.getId());
                    return User.reconstruct(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getUsername(),
                            user.getPassword(),
                            user.getAddress(),
                            roles,
                            user.isEnabled(),
                            user.getCreatedAt(),
                            user.getUpdatedAt()
                    );
                })
                .toList();
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT COUNT(*) FROM users")
                .query(Long.class)
                .single();
    }

    @Override
    public void delete(User user) {
        // Business rule: Check if user owns any restaurants
        Long restaurantCount = jdbcClient.sql("SELECT COUNT(*) FROM restaurants WHERE owner_id = :userId")
                .param("userId", user.getId())
                .query(Long.class)
                .single();

        if (restaurantCount > 0) {
            throw new UserHasRestaurantsException(restaurantCount);
        }

        // Get address ID before deleting user
        Optional<Long> addressIdOpt = jdbcClient.sql("SELECT address_id FROM users WHERE id = :id")
                .param("id", user.getId())
                .query(Long.class)
                .optional();

        // Delete user (roles will be cascade deleted by database FK)
        jdbcClient.sql("DELETE FROM users WHERE id = :id")
                .param("id", user.getId())
                .update();

        // Delete address if it exists
        addressIdOpt.ifPresent(addressId -> {
            if (addressId != null) {
                jdbcClient.sql("DELETE FROM addresses WHERE id = :addressId")
                        .param("addressId", addressId)
                        .update();
            }
        });
    }

    @Override
    public boolean existsByUsername(String username) {
        return jdbcClient.sql("SELECT COUNT(*) FROM users WHERE username = :username")
                .param("username", username)
                .query(Long.class)
                .single() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return jdbcClient.sql("SELECT COUNT(*) FROM users WHERE email = :email")
                .param("email", email)
                .query(Long.class)
                .single() > 0;
    }

    // Helper methods
    private List<Role> findRolesByUserId(Long userId) {
        return jdbcClient.sql("""
                            SELECT r.id, r.name
                            FROM roles r
                            JOIN user_roles ur ON ur.role_id = r.id
                            WHERE ur.user_id = :userId
                        """)
                .param("userId", userId)
                .query((rs, rowNum) -> Role.fromId(rs.getLong("id")))
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
}
