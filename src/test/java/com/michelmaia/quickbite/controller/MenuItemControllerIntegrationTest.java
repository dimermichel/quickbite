package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginResponse;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.CreateMenuItemRequest;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.MenuItemResponse;
import com.michelmaia.quickbite.presentation.rest.menuitem.dto.UpdateMenuItemRequest;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
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
        LoginRequest loginRequest = new LoginRequest("testowner", "admin");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
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
        CreateMenuItemRequest createRequest = new CreateMenuItemRequest(
                1L, // restaurantId
                "New Test Menu Item",
                "Delicious test item",
                9.99,
                "https://image.com",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateMenuItemRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.POST,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("New Test Menu Item");
    }

    @Test
    void shouldNotCreateMenuItemForNonExistentRestaurant() {
        // Given
        CreateMenuItemRequest createRequest = new CreateMenuItemRequest(
                999L, // Non-existent restaurantId
                "Invalid Menu Item",
                "This should fail",
                9.99,
                "https://image.com",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateMenuItemRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.POST,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotCreateMenuItemWithInvalidData() {
        // Given
        CreateMenuItemRequest createRequest = new CreateMenuItemRequest(
                1L, // Valid restaurantId
                "", // Invalid name (empty)
                "This should fail",
                -5.00, // Invalid price (negative)
                "https://image.com",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateMenuItemRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.POST,
                request,
                String.class // String to capture error message
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotCreateMenuItemWithoutAuthentication() {
        // Given
        CreateMenuItemRequest createRequest = new CreateMenuItemRequest(
                1L, // restaurantId
                "New Test Menu Item",
                "Delicious test item",
                9.99,
                "https://image.com",
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No auth token set
        HttpEntity<CreateMenuItemRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items",
                HttpMethod.POST,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldFindAllAvailableMenuItemsByRestaurant() {
        // Given
        Long restaurantId = 1L;
        Boolean available = true;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemResponse[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant/available?available=" + available + "&restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemResponse[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldFindAllMenuItemsByRestaurant() {
        // Given
        Long restaurantId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemResponse[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant?restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemResponse[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldFindMenuItemById() {
        // Given
        Long menuItemId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/" + menuItemId,
                HttpMethod.GET,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(menuItemId);
    }

    @Test
    void shouldNotFindMenuItemByInvalidId() {
        // Given
        Long invalidMenuItemId = 999L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/" + invalidMenuItemId,
                HttpMethod.GET,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFindMenuItemsByNameAndRestaurant() {
        // Given
        Long restaurantId = 1L;
        String name = "Pizza";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<MenuItemResponse[]> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/restaurant/search?name=" + name + "&restaurantId=" + restaurantId,
                HttpMethod.GET,
                request,
                MenuItemResponse[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
    }

    @Test
    void shouldUpdateMenuItem() {
        // Given
        Long menuItemId = 1L;
        UpdateMenuItemRequest updateRequest = new UpdateMenuItemRequest(
                "Updated Test Menu Item",
                "Updated description",
                12.99,
                "https://updated-image.com",
                false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateMenuItemRequest> request = new HttpEntity<>(updateRequest, headers);

        // When
        ResponseEntity<MenuItemResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/menu-items/" + menuItemId,
                HttpMethod.PUT,
                request,
                MenuItemResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated Test Menu Item");
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
