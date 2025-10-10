package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.dto.LoginDTO;
import com.michelmaia.quickbite.dto.SessionDTO;
import com.michelmaia.quickbite.model.User;
import com.michelmaia.quickbite.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    void setUp() {
        // Login to get auth token
        LoginDTO loginDTO = new LoginDTO("admin", "admin");
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
    void shouldRegisterUserViaPublicEndpoint() {
        // Given
        // Use raw JSON to include a password field and avoid serialization issues @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String userJson = """
                {
                    "name": "New User",
                    "email": "newuser@test.com",
                    "username": "testuser",
                    "password": "testpassword",
                    "address": {
                        "street": "123 Test Street",
                        "city": "Test City",
                        "state": "Test State",
                        "zipCode": "12345"
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        // Given
        // Use raw JSON to include a password field and avoid serialization issues @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String userJson = """
                {
                    "name": "New User",
                    "email": "owner@test.com",
                    "username": "testowner",
                    "password": "testpassword",
                    "address": {
                        "street": "123 Test Street",
                        "city": "Test City",
                        "state": "Test State",
                        "zipCode": "12345"
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

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
        // Use raw JSON to include a password field and avoid serialization issues @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String userJson = """
                {
                    "name": "New User",
                    "email": "owner@test.com",
                    "username": "newnewuser",
                    "password": "testpassword",
                    "address": {
                        "street": "123 Test Street",
                        "city": "Test City",
                        "state": "Test State",
                        "zipCode": "12345"
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

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
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/" + userId,
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("testowner");
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
    void shouldSaveNewUserWhenAuthenticated() {
        // Given
        // Use raw JSON to include a password field and avoid serialization issues @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String userJson = """
                {
                  "name": "new user",
                  "email": "newuser@test.com",
                  "username": "newuser",
                  "password": "newuser",
                  "roles": [
                    {
                        "id": 2
                    },
                    {
                        "id": 3
                    }
                  ],
                  "enabled": true,
                  "address": {
                    "street": "123 User Street",
                    "city": "Miami",
                    "state": "FL",
                    "zipCode": "45879"
                  }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/api/users",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldUpdateUserWhenAuthenticated() {
        // Given
        User existingUser = userRepository.findByUsername("testowner").orElseThrow();
        Long userId = existingUser.getId();

        // Use raw JSON to include a password field and avoid serialization issues @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String updatedUserJson = """
                {
                  "name": "updated owner",
                  "email": "newemail@test.com",
                  "username": "testowner",
                  "password": "owner",
                  "roles": [
                        {
                            "id": 2
                        }
                  ],
                  "enabled": true,
                  "address": {
                    "street": "456 Updated Street",
                    "city": "Updated City",
                    "state": "Updated State",
                    "zipCode": "67890"
                  }
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(updatedUserJson, headers);
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
        String userJson = """
                {
                    "name": "New User",
                    "email": "newuser@test.com",
                    "username": "testuser",
                    "password": "testpassword",
                    "address": {
                        "street": "123 Test Street",
                        "city": "Test City",
                        "state": "Test State",
                        "zipCode": "12345"
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        ResponseEntity<Void> createResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                Void.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User user = userRepository.findByUsername("testuser").orElseThrow();
        Long userId = user.getId();

        headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<?> deleteRequest = new HttpEntity<>(headers);
        // When
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

