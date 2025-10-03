package com.michelmaia.quickbite.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final SecurityConfig securityConfig;
    private static final String ADMIN_ROLE = "ADMIN";

    public WebSecurityConfig(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .addFilterAfter(new JWTFilter(securityConfig), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/change-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll() // Allow registration
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole(ADMIN_ROLE) // Only admins can create users directly
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("USER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, "/api/restaurants").hasAnyRole("USER", "OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, "/api/restaurants/**").hasAnyRole("OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/api/restaurants/**").hasAnyRole("OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/api/restaurants/**").hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, "/api/menu-items").hasAnyRole("USER", "OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, "/api/menu-items/**").hasAnyRole("OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.PUT, "/api/menu-items/**").hasAnyRole("OWNER", ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, "/api/menu-items/**").hasRole(ADMIN_ROLE)
                        .anyRequest().authenticated()
                ).build();
    }
}