package com.michelmaia.quickbite.domain.user.repository;

import com.michelmaia.quickbite.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository Interface - Defined by the domain, implemented by infrastructure
 * This is a port that will be implemented by the infrastructure layer
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findAll(int page, int size);
    
    List<User> findByRole(Long roleId, int page, int size);
    
    long count();
    
    void delete(User user);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
