package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.dto.LoginDTO;
import com.michelmaia.quickbite.dto.RestaurantDTO;
import com.michelmaia.quickbite.dto.SessionDTO;
import com.michelmaia.quickbite.model.Address;
import com.michelmaia.quickbite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RestaurantControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String authToken;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        // Login to get auth token
        LoginDTO loginDTO = new LoginDTO("testowner", "admin");
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );

        ownerId = userRepository.findByUsername("testowner")
                .orElseThrow(() -> new IllegalStateException("Test user not found"))
                .getId();

        if (loginResponse.getStatusCode() == HttpStatus.OK && loginResponse.getBody() != null) {
            authToken = loginResponse.getBody().token();
        } else {
            throw new IllegalStateException("Failed to authenticate test user. Status: " + loginResponse.getStatusCode());
        }
    }

    @Test
    void shouldCreateRestaurant() {
        // Given
        Address address = new Address();
        address.setStreet("123 Test Street");
        address.setCity("Test City");
        address.setState("Test State");
        address.setZipCode("12345");

        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .ownerId(ownerId)
                .name("New Test Restaurant")
                .cuisine("Italian")
                .address(address)
                .openingHours("9:00-22:00")
                .rating(4.5)
                .isOpen(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<RestaurantDTO> request = new HttpEntity<>(restaurantDTO, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldGetRestaurantById() {
        // Given
        Long restaurantId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<RestaurantDTO> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + restaurantId,
                HttpMethod.GET,
                request,
                RestaurantDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(restaurantId);
    }

    @Test
    void shouldGetAllRestaurants() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldGetRestaurantByCuisine() {
        // Given
        String cuisine = "Italian";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/by-cuisine?cuisine=" + cuisine,
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(cuisine);
    }

    @Test
    void shouldGetRestaurantByRating() {
        // Given
        Double minRating = 4.0;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/by-rating?minRating=" + minRating,
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnNotFoundForNonExistentRestaurant() {
        // Given
        Long nonExistentId = 9999L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<RestaurantDTO> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + nonExistentId,
                HttpMethod.GET,
                request,
                RestaurantDTO.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateRestaurant() {
        // Given
        Long restaurantId = 1L;

        RestaurantDTO updateDTO = RestaurantDTO.builder()
                .id(restaurantId)
                .name("Updated Restaurant Name")
                .cuisine("Mexican")
                .openingHours("10:00-23:00")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<RestaurantDTO> request = new HttpEntity<>(updateDTO, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.PUT,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldDeleteRestaurant() {
        // Given
        Long restaurantId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + restaurantId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}