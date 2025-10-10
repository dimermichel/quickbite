package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.dto.LoginDTO;
import com.michelmaia.quickbite.dto.MenuItemDTO;
import com.michelmaia.quickbite.dto.SessionDTO;
import com.michelmaia.quickbite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MenuItemControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    void setUp() {
        LoginDTO loginDTO = new LoginDTO("testowner", "admin");
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );

        if (loginResponse.getStatusCode() == HttpStatus.OK && loginResponse.getBody() != null) {
            authToken = loginResponse.getBody().token();
        } else {
            throw new IllegalStateException("Failed to authenticate test user. Status: " + loginResponse.getStatusCode());
        }
    }

    @Test
    void shouldCreateMenuItem() {
        // Given
        MenuItemDTO menuItemDTO = MenuItemDTO.builder()
                .restaurantId(1L) // Assuming a restaurant with ID 1 exists and belongs to testowner
                .name("New Test Menu Item")
                .description("Delicious test item")
                .price(9.99)
                .imageUrl("https://image.com")
                .isAvailable(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<MenuItemDTO> request = new HttpEntity<>(menuItemDTO, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldFindAllAvailableMenuItemsByRestaurant() {
        // Given
        Long restaurantId = 1L; // Assuming a restaurant with ID 1 exists
        Boolean available = true;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemDTO[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant/available?available=" + available + "&restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldFindAllMenuItemsByRestaurant() {
        // Given
        Long restaurantId = 1L; // Assuming a restaurant with ID 1 exists

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemDTO[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant?restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldFindMenuItemById() {
        // Given
        Long menuItemId = 1L; // Assuming a menu item with ID 1 exists

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemDTO> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/" + menuItemId,
                HttpMethod.GET,
                request,
                MenuItemDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(menuItemId);
    }

    @Test
    void shouldFindMenuItemsByNameAndRestaurant() {
        // Given
        Long restaurantId = 1L; // Assuming a restaurant with ID 1 exists
        String name = "Pizza"; // Assuming there are menu items with "Pizza" in the name

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemDTO[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant/search?name=" + name + "&restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemDTO[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldUpdateMenuItem() {
        // Given
        Long menuItemId = 1L; // Assuming a menu item with ID 1 exists
        MenuItemDTO menuItemDTO = MenuItemDTO.builder()
                .id(menuItemId)
                .restaurantId(1L) // Assuming a restaurant with ID 1 exists and belongs to testowner
                .name("Updated Test Menu Item")
                .description("Updated description")
                .price(12.99)
                .imageUrl("https://updated-image.com")
                .isAvailable(false)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<MenuItemDTO> request = new HttpEntity<>(menuItemDTO, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.PUT,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteMenuItem() {
        Long menuItemId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/" + menuItemId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
