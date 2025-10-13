package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginResponse;
import com.michelmaia.quickbite.presentation.rest.user.dto.CreateUserRequest;
import com.michelmaia.quickbite.presentation.rest.user.dto.UpdateUserRequest;
import com.michelmaia.quickbite.presentation.rest.user.dto.UserResponse;
import com.michelmaia.quickbite.domain.user.entity.User;
import com.michelmaia.quickbite.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    void setUp() {
        LoginRequest loginRequest = new LoginRequest("admin", "admin");
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
    void shouldRegisterUserViaPublicEndpoint() {
        // Given
        CreateUserRequest.AddressRequest address = new CreateUserRequest.AddressRequest(
                "123 Test Street", "Test City", "Test State", "12345"
        );
        CreateUserRequest createRequest = new CreateUserRequest(
                "New User",
                "newuser@test.com",
                "testuser",
                "testpassword",
                address,
                null,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        // Given
        CreateUserRequest.AddressRequest address = new CreateUserRequest.AddressRequest(
                "123 Test Street", "Test City", "Test State", "12345"
        );
        CreateUserRequest createRequest = new CreateUserRequest(
                "New User",
                "different@test.com",
                "testowner", // existing username
                "testpassword",
                address,
                null,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() {
        // Given
        CreateUserRequest.AddressRequest address = new CreateUserRequest.AddressRequest(
                "123 Test Street", "Test City", "Test State", "12345"
        );
        CreateUserRequest createRequest = new CreateUserRequest(
                "New User",
                "owner@test.com", // existing email
                "newnewuser",
                "testpassword",
                address,
                null,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldGetAllUsersWhenAuthenticated() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("admin");
        assertThat(response.getBody()).contains("owner@test.com");
    }

    @Test
    void shouldGetUserByIdWhenAuthenticated() {
        // Given
        User user = userRepository.findByUsername("testowner").orElseThrow();
        Long userId = user.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.GET,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testowner");
    }

    @Test
    void shouldNotGetUserByIdWhenNotAuthenticated() {
        // Given
        User user = userRepository.findByUsername("testowner").orElseThrow();
        Long userId = user.getId();
        HttpEntity<?> request = new HttpEntity<>(new HttpHeaders());

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldCreateNewUserWhenAuthenticated() {
        // Given
        CreateUserRequest.AddressRequest address = new CreateUserRequest.AddressRequest(
                "123 User Street", "Miami", "FL", "45879"
        );
        CreateUserRequest createRequest = new CreateUserRequest(
                "new user",
                "newuser@test.com",
                "newuser",
                "newuser",
                address,
                List.of(2L, 3L), // roleIds
                true // enabled
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, headers);

        // When
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                request,
                UserResponse.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("newuser");
    }

    @Test
    void shouldUpdateUserWhenAuthenticated() {
        // Given
        User existingUser = userRepository.findByUsername("testowner").orElseThrow();
        Long userId = existingUser.getId();

        UpdateUserRequest.AddressRequest address = new UpdateUserRequest.AddressRequest(
                "456 Updated Street", "Updated City", "Updated State", "67890"
        );
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "updated owner",
                "newemail@test.com",
                "newpassword", // optional password
                address
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateUserRequest> request = new HttpEntity<>(updateRequest, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.PUT,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("updated owner");
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@test.com");
        assertThat(updatedUser.getAddress().getStreet()).isEqualTo("456 Updated Street");
    }

    @Test
    void shouldNotDeleteUserThatHasRestaurant() {
        // Given
        User user = userRepository.findByUsername("testowner").orElseThrow();
        Long userId = user.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.DELETE,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldDeleteUserThatHasNoRestaurant() {
        // Given - Create a user first
        CreateUserRequest.AddressRequest address = new CreateUserRequest.AddressRequest(
                "123 Test Street", "Test City", "Test State", "12345"
        );
        CreateUserRequest createRequest = new CreateUserRequest(
                "New User",
                "newuser@test.com",
                "testuser",
                "testpassword",
                address,
                null,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createRequest, headers);

        ResponseEntity<UserResponse> createResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                UserResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User user = userRepository.findByUsername("testuser").orElseThrow();
        Long userId = user.getId();

        // When - Delete the user
        headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> deleteRequest = new HttpEntity<>(headers);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.DELETE,
                deleteRequest,
                Void.class
        );

        // Then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userRepository.findById(userId)).isNotPresent();
    }
}
