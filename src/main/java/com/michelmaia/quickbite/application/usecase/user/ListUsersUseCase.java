package com.michelmaia.quickbite.application.usecase.user;

import com.michelmaia.quickbite.application.dto.PageResponseDTO;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;

import java.util.List;

/**
 * Use Case: List users with pagination and optional role filtering
 */
public class ListUsersUseCase {
    
    private final UserRepository userRepository;
    
    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Execute the list users use case
     * 
     * @param query The query parameters for listing users
     * @return PageResponseDTO containing the list of users and pagination info
     */
    public PageResponseDTO<User> execute(ListUsersQuery query) {
        // Fetch users based on role filter if provided
        List<User> users;
        long totalCount;
        
        if (query.roleId() != null) {
            users = userRepository.findByRole(query.roleId(), query.page(), query.size());
            // For simplicity, we'll get the total count by querying all and counting
            // In a real implementation, you'd want a countByRole method in the repository
            totalCount = userRepository.findByRole(query.roleId(), 0, Integer.MAX_VALUE).size();
        } else {
            users = userRepository.findAll(query.page(), query.size());
            totalCount = userRepository.count();
        }
        
        // Build page response
        return new PageResponseDTO<>(
            users,
            query.page(),
            query.size(),
            totalCount
        );
    }
    
    /**
     * Query object for listing users
     * 
     * @param page The page number (0-indexed)
     * @param size The number of items per page
     * @param roleId Optional role ID to filter by
     */
    public record ListUsersQuery(
        int page,
        int size,
        Long roleId
    ) {
        public ListUsersQuery {
            // Validation
            if (page < 0) {
                throw new IllegalArgumentException("Page must be non-negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be positive");
            }
            if (size > 100) {
                throw new IllegalArgumentException("Size must not exceed 100");
            }
        }
        
        // Convenience constructor without role filter
        public ListUsersQuery(int page, int size) {
            this(page, size, null);
        }
    }
}
