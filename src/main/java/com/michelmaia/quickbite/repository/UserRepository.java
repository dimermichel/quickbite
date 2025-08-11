package com.michelmaia.quickbite.repository;

import com.michelmaia.quickbite.dto.PageResponseDTO;
import com.michelmaia.quickbite.model.User;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    PageResponseDTO<User> findAllPaginated(Pageable pageable, Optional<Long> roleId);

    User save(User user);

    User update(User user, Long id);

    Integer deleteById(Long id);
}
