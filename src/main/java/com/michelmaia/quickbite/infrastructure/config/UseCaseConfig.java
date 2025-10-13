package com.michelmaia.quickbite.infrastructure.config;

import com.michelmaia.quickbite.application.service.PasswordEncoder;
import com.michelmaia.quickbite.application.service.TokenGenerator;
import com.michelmaia.quickbite.application.usecase.auth.ChangePasswordUseCase;
import com.michelmaia.quickbite.application.usecase.auth.LoginUseCase;
import com.michelmaia.quickbite.application.usecase.menuitem.*;
import com.michelmaia.quickbite.application.usecase.restaurant.CreateRestaurantUseCase;
import com.michelmaia.quickbite.application.usecase.restaurant.GetRestaurantUseCase;
import com.michelmaia.quickbite.application.usecase.restaurant.ListRestaurantsUseCase;
import com.michelmaia.quickbite.application.usecase.restaurant.UpdateRestaurantUseCase;
import com.michelmaia.quickbite.application.usecase.restaurant.DeleteRestaurantUseCase;
import com.michelmaia.quickbite.application.usecase.user.*;
import com.michelmaia.quickbite.domain.menuitem.repository.MenuItemRepository;
import com.michelmaia.quickbite.domain.restaurant.repository.RestaurantRepository;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: Wire up use cases with their dependencies
 * This is where we inject adapters into use cases
 */
@Configuration
public class UseCaseConfig {
    
    // ========== Authentication Use Cases ==========
    
    @Bean
    public LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenGenerator);
    }
    
    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return new ChangePasswordUseCase(userRepository, passwordEncoder);
    }
    
    // ========== User Management Use Cases ==========
    
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCase(userRepository, passwordEncoder);
    }
    
    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(userRepository, passwordEncoder);
    }
    
    @Bean
    public GetUserUseCase getUserUseCase(UserRepository userRepository) {
        return new GetUserUseCase(userRepository);
    }
    
    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersUseCase(userRepository);
    }
    
    @Bean
    public UpdateUserUseCase updateUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return new UpdateUserUseCase(userRepository, passwordEncoder);
    }
    
    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepository userRepository) {
        return new DeleteUserUseCase(userRepository);
    }

    // ========== Restaurant Use Cases ==========

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository) {
        return new CreateRestaurantUseCase(restaurantRepository, userRepository);
    }

    @Bean
    public GetRestaurantUseCase getRestaurantUseCase(
            RestaurantRepository restaurantRepository) {
        return new GetRestaurantUseCase(restaurantRepository);
    }

    @Bean
    public ListRestaurantsUseCase listRestaurantsUseCase(
            RestaurantRepository restaurantRepository) {
        return new ListRestaurantsUseCase(restaurantRepository);
    }

    @Bean
    public UpdateRestaurantUseCase updateRestaurantUseCase(
            RestaurantRepository restaurantRepository) {
        return new UpdateRestaurantUseCase(restaurantRepository);
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(
            RestaurantRepository restaurantRepository) {
        return new DeleteRestaurantUseCase(restaurantRepository);
    }

    // ========== Menu Item Use Cases ==========

    @Bean
    public CreateMenuItemUseCase createMenuItemUseCase(
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository) {
        return new CreateMenuItemUseCase(menuItemRepository, restaurantRepository);
    }

    @Bean
    public GetMenuItemUseCase getMenuItemUseCase(
            MenuItemRepository menuItemRepository) {
        return new GetMenuItemUseCase(menuItemRepository);
    }

    @Bean
    public ListMenuItemsUseCase listMenuItemsUseCase(
            MenuItemRepository menuItemRepository) {
        return new ListMenuItemsUseCase(menuItemRepository);
    }

    @Bean
    public UpdateMenuItemUseCase updateMenuItemUseCase(
            MenuItemRepository menuItemRepository) {
        return new UpdateMenuItemUseCase(menuItemRepository);
    }

    @Bean
    public DeleteMenuItemUseCase deleteMenuItemUseCase(
            MenuItemRepository menuItemRepository) {
        return new DeleteMenuItemUseCase(menuItemRepository);
    }
}
