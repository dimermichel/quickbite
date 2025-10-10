package com.michelmaia.quickbite.controller;

import com.michelmaia.quickbite.BaseIntegrationTest;
import com.michelmaia.quickbite.dto.ChangePasswordDTO;
import com.michelmaia.quickbite.dto.LoginDTO;
import com.michelmaia.quickbite.dto.SessionDTO;
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
        LoginDTO loginDTO = new LoginDTO("testowner", "admin");
        // When
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );
        // Then
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotNull();
    }

    @Test
    void shouldNotLoginWithWrongPassword() {
        // Given
        LoginDTO loginDTO = new LoginDTO("testowner", "wrongpassword");
        // When
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );
        // Then
        assertThat(loginResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldNotLoginWithNonExistentUser() {
        // Given
        LoginDTO loginDTO = new LoginDTO("nonexistentuser", "somepassword");
        // When
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );
        // Then
        assertThat(loginResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // Given
        LoginDTO loginDTO = new LoginDTO("testowner", "admin");
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        String authToken = loginResponse.getBody().token();

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("testowner", "admin", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<ChangePasswordDTO> request = new HttpEntity<>(changePasswordDTO, headers);

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
        LoginDTO newLoginDTO = new LoginDTO("testowner", "newadmin");
        ResponseEntity<SessionDTO> newLoginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                newLoginDTO,
                SessionDTO.class
        );

        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newLoginResponse.getBody()).isNotNull();
        assertThat(newLoginResponse.getBody().token()).isNotNull();
    }

    @Test
    void shouldNotChangePasswordWithWrongCurrentPasswordAuthenticated() {
        // Given
        LoginDTO loginDTO = new LoginDTO("testowner", "admin");
        ResponseEntity<SessionDTO> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/api/login",
                loginDTO,
                SessionDTO.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        String authToken = loginResponse.getBody().token();

        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("testowner", "wrongpassword", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<ChangePasswordDTO> request = new HttpEntity<>(changePasswordDTO, headers);

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
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("testowner", "wrongpassword", "newadmin");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChangePasswordDTO> request = new HttpEntity<>(changePasswordDTO, headers);

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
