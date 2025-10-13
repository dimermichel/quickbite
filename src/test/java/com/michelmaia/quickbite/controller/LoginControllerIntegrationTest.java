package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.ChangePasswordRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginRequest;
import com.michelmaia.quickbite.presentation.rest.auth.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LoginControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testowner", "admin");
        // When
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );
        // Then
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotNull();
    }

    @Test
    void shouldNotLoginWithWrongPassword() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testowner", "wrongpassword");
        // When
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );
        // Then
        assertThat(loginResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldNotLoginWithNonExistentUser() {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistentuser", "somepassword");
        // When
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );
        // Then
        assertThat(loginResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testowner", "admin");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        String authToken = loginResponse.getBody().token();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("testowner", "admin", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<ChangePasswordRequest> request = new HttpEntity<>(changePasswordRequest, headers);

        // When
        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                getBaseUrl() + "/api/change-password",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify that the user can log in with the new password
        LoginRequest newLoginRequest = new LoginRequest("testowner", "newadmin");
        ResponseEntity<LoginResponse> newLoginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                newLoginRequest,
                LoginResponse.class
        );

        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newLoginResponse.getBody()).isNotNull();
        assertThat(newLoginResponse.getBody().token()).isNotNull();
    }

    @Test
    void shouldNotChangePasswordWithWrongCurrentPasswordAuthenticated() {
        // Given
        LoginRequest loginRequest = new LoginRequest("testowner", "admin");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginRequest,
                LoginResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        String authToken = loginResponse.getBody().token();

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("testowner", "wrongpassword", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<ChangePasswordRequest> request = new HttpEntity<>(changePasswordRequest, headers);

        // When
        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                getBaseUrl() + "/api/change-password",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(changePasswordResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldNotChangePasswordWithWrongCurrentPasswordUnauthenticated() {
        // Given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("testowner", "wrongpassword", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChangePasswordRequest> request = new HttpEntity<>(changePasswordRequest, headers);

        // When
        ResponseEntity<Void> changePasswordResponse = restTemplate.exchange(
                getBaseUrl() + "/api/change-password",
                HttpMethod.POST,
                request,
                Void.class
        );

        // Then
        assertThat(changePasswordResponse.getStatusCode().is4xxClientError()).isTrue();
    }
}
