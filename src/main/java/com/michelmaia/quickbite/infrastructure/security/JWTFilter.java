package com.michelmaia.quickbite.infrastructure.security;
import com.michelmaia.quickbite.domain.user.entity.Role;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter {

    private final SecurityConfig securityConfig;

    public JWTFilter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Get the token from the request header with AUTHORIZATION
        String token =  request.getHeader(JWTCreator.HEADER_AUTHORIZATION);
        // This implementation only validates the integrity of the token
        try {
            if(token!=null && !token.isEmpty()) {
                JWTObject tokenObject = JWTCreator.create(token,securityConfig.getPrefix()
                        , securityConfig.getKey());

                List<SimpleGrantedAuthority> authorities = authorities(tokenObject.getRoles());

                UsernamePasswordAuthenticationToken userToken =
                        new UsernamePasswordAuthenticationToken(
                                tokenObject.getSubject(),
                                null,
                                authorities);

                SecurityContextHolder.getContext().setAuthentication(userToken);

            }else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
    }
    private List<SimpleGrantedAuthority> authorities(List<Role> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

}
