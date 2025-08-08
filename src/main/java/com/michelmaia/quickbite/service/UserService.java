package com.michelmaia.quickbite.service;

import com.michelmaia.quickbite.model.Role;
import com.michelmaia.quickbite.model.User;
import com.michelmaia.quickbite.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers(int page, int size, Long roleId) {
        int offset = (page - 1) * size;
        if (roleId == null) {
            return userRepository.findAll(size, offset);
        }
        return userRepository.findAll(size, offset, roleId);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }

        // don't allow setting an admin role during registration
        if (user.getRoles() != null) {
            user.getRoles().removeIf(role ->
                    "ADMIN".equalsIgnoreCase(role.getName()) ||
                            "ROLE_ADMIN".equalsIgnoreCase(role.getName())
            );
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // Set the default role as USER if no roles are provided
            user.setRoles(List.of(new Role(1L, "USER")));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true); // Set default enabled status
        var savedUser = userRepository.save(user);
        if (savedUser == null) {
            throw new IllegalStateException("User could not be registered");
        }
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var save = userRepository.save(user);
        if (save == null) {
            throw new IllegalStateException("User could not be saved");
        }
    }

    public void updateUserPassword(User user, String oldPassword, String newPassword) {
        Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());
        if (existingUserOpt.isEmpty()) {
            throw new IllegalStateException("User not found");
        }

        User existingUser = existingUserOpt.get();
        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            throw new IllegalStateException("Old password is incorrect");
        }

        // Update the password
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        var updatedUser = userRepository.update(existingUser, existingUser.getId());
        if (updatedUser == null) {
            throw new IllegalStateException("Password could not be updated");
        }
    }

    public void updateUser(User user, Long id) {
        var update = userRepository.update(user, id);
        if (update == null) {
            throw new IllegalStateException("User with id " + id + " could not be updated");
        }
    }

    public void deleteUserById(Long id) {
        var delete = userRepository.deleteById(id);
        if (delete == null || delete == 0) {
            throw new IllegalStateException("User with id " + id + " could not be deleted");
        }
    }
}