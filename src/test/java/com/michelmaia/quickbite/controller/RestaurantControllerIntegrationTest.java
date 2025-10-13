package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.application.dto.PageResponseDTO;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginResponse;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.CreateRestaurantRequest;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.RestaurantResponse;
import com.michelmaia.quickbite.presentation.rest.restaurant.dto.UpdateRestaurantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.core.ParameterizedTypeReference;

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
        LoginRequest loginRequest = new LoginRequest("testowner", "admin");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
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
        var addressRequest = new CreateRestaurantRequest.AddressRequest(
                "123 Test St",
                "Test City",
                "TS",
                "12345"
        );

        CreateRestaurantRequest createRequest = new CreateRestaurantRequest(
                ownerId,
                "New Test Restaurant",
                "Italian",
                addressRequest,
                "9:00-22:00",
                4.5,
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateRestaurantRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                RestaurantResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("New Test Restaurant");

        // Verify restaurant was actually created
        ResponseEntity<PageResponseDTO<RestaurantResponse>> getAllResponse = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<PageResponseDTO<RestaurantResponse>>() {
                }
        );
        assertThat(getAllResponse.getBody().getData())
                .extracting("name")
                .contains("New Test Restaurant");
    }

    @Test
    void shouldNotCreateRestaurantWithInvalidData() {
        // Given - Missing name and invalid rating
        var addressRequest = new CreateRestaurantRequest.AddressRequest(
                "123 Test St",
                "Test City",
                "TS",
                "12345"
        );

        CreateRestaurantRequest createRequest = new CreateRestaurantRequest(
                ownerId,
                null, // Invalid name - empty string
                "Italian",
                addressRequest,
                "9:00-22:00",
                6.0, // Invalid rating
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateRestaurantRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Restaurant name is required");
        assertThat(response.getBody()).contains("Rating must be at most 5.0");
    }

    @Test
    void shouldNotCreateRestaurantWithUserThatDontHaveOwnerOrAdminRole() {
        // Given - Login as regular user
        LoginRequest loginRequest = new LoginRequest("testuser", "admin");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );

        String userAuthToken;
        if (loginResponse.getStatusCode() == HttpStatus.OK && loginResponse.getBody() != null) {
            userAuthToken = loginResponse.getBody().token();
        } else {
            throw new IllegalStateException("Failed to authenticate test user. Status: " + loginResponse.getStatusCode());
        }

        var addressRequest = new CreateRestaurantRequest.AddressRequest(
                "123 Test St",
                "Test City",
                "TS",
                "12345"
        );

        CreateRestaurantRequest createRequest = new CreateRestaurantRequest(
                ownerId,
                "New Test Restaurant",
                "Italian",
                addressRequest,
                "9:00-22:00",
                4.5,
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userAuthToken);
        HttpEntity<CreateRestaurantRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotCreateRestaurantWithUserThatDoesNotHaveOwnerOrAdminRole() {
        // Given - ID of a user without an OWNER or ADMIN role
        var userId = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new IllegalStateException("Test user not found"))
                .getId();


        var addressRequest = new CreateRestaurantRequest.AddressRequest(
                "123 Test St",
                "Test City",
                "TS",
                "12345"
        );

        CreateRestaurantRequest createRequest = new CreateRestaurantRequest(
                userId,
                "New Test Restaurant",
                "Italian",
                addressRequest,
                "9:00-22:00",
                4.5,
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateRestaurantRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotCreateRestaurantWithoutAuth() {
        // Given
        var addressRequest = new CreateRestaurantRequest.AddressRequest(
                "123 Test St",
                "Test City",
                "TS",
                "12345"
        );

        CreateRestaurantRequest createRequest = new CreateRestaurantRequest(
                ownerId,
                "New Test Restaurant",
                "Italian",
                addressRequest,
                "9:00-22:00",
                4.5,
                true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // No auth token set
        HttpEntity<CreateRestaurantRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldGetRestaurantById() {
        // Given
        Long restaurantId = 1L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + restaurantId,
                HttpMethod.GET,
                request,
                RestaurantResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(restaurantId);
        assertThat(response.getBody().name()).isNotNull();
        assertThat(response.getBody().cuisine()).isNotNull();
    }


    @Test
    void shouldGetAllRestaurants() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<PageResponseDTO<RestaurantResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<PageResponseDTO<RestaurantResponse>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
    }


    @Test
    void shouldGetRestaurantByCuisine() {
        // Given
        String cuisine = "Italian";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<PageResponseDTO<RestaurantResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/by-cuisine?cuisine=" + cuisine,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<PageResponseDTO<RestaurantResponse>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().get(0).cuisine()).isEqualTo(cuisine);
    }


    @Test
    void shouldGetRestaurantByRating() {
        // Given
        Double minRating = 4.0;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<PageResponseDTO<RestaurantResponse>> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/by-rating?minRating=" + minRating,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<PageResponseDTO<RestaurantResponse>>() {
                }
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().get(0).rating()).isGreaterThanOrEqualTo(minRating);
    }


    @Test
    void shouldReturnNotFoundForNonExistentRestaurant() {
        // Given
        Long nonExistentId = 9999L;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + nonExistentId,
                HttpMethod.GET,
                request,
                RestaurantResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateRestaurant() {
        // Given
        Long restaurantId = 1L;

        // First, fetch the existing restaurant to get all fields
        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.setBearerAuth(authToken);
        ResponseEntity<RestaurantResponse> getResponse = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + restaurantId,
                HttpMethod.GET,
                new HttpEntity<>(getHeaders),
                RestaurantResponse.class
        );

        // Verify the restaurant exists before updating
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();

        RestaurantResponse existing = getResponse.getBody();

        // Create UpdateRestaurantRequest instead of RestaurantDTO
        var addressRequest = new UpdateRestaurantRequest.AddressRequest(
                existing.address().street(),
                existing.address().city(),
                existing.address().state(),
                existing.address().zipCode()
        );

        UpdateRestaurantRequest updateRequest = new UpdateRestaurantRequest(
                "Updated Restaurant Name",
                "Mexican",
                addressRequest,
                "10:00-23:00",
                existing.rating(),
                existing.isOpen()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateRestaurantRequest> request = new HttpEntity<>(updateRequest, headers);

        // When - Fix URL to include restaurant ID
        ResponseEntity<RestaurantResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/restaurants/" + restaurantId,
                HttpMethod.PUT,
                request,
                RestaurantResponse.class
        );

        // Then - Controller returns 200 OK with RestaurantResponse
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated Restaurant Name");
        assertThat(response.getBody().cuisine()).isEqualTo("Mexican");
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