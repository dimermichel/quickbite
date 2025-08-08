package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAll(int size, int offset);

    List<User> findAll(int size, int offset, Long roleId);

    User save(User user);

    User update(User user, Long id);

    Integer deleteById(Long id);
}
