package com.michelmaia.quickbite.infrastructure.security;

import com.michelmaia.quickbite.domain.user.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JWTCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTCreator.class);

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities";

    // Private constructor to prevent instantiation
    private JWTCreator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a JWT token from the given parameters
     */
    public static String create(String prefix, String key, JWTObject jwtObject) {
        validateCreateParameters(prefix, key, jwtObject);

        try {
            SecretKey secretKey = createSecretKey(key);

            String token = Jwts.builder()
                    .subject(jwtObject.getSubject())
                    .issuedAt(jwtObject.getIssuedAt())
                    .expiration(jwtObject.getExpiration())
                    .claim(ROLES_AUTHORITIES, processRoles(jwtObject.getRoles()))
                    .signWith(secretKey)
                    .compact();

            return prefix + " " + token;
        } catch (Exception e) {
            LOGGER.error("Error creating JWT token", e);
            throw new IllegalStateException("Failed to create JWT token", e);
        }
    }

    /**
     * Parses a JWT token and extracts the JWT object
     */
    public static JWTObject create(String token, String prefix, String key) {
        validateParseParameters(token, prefix, key);

        try {
            String cleanToken = sanitizeToken(token, prefix);
            SecretKey secretKey = createSecretKey(key);

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(cleanToken)
                    .getPayload();

            return buildJWTObject(claims);

        } catch (ExpiredJwtException e) {
            LOGGER.warn("JWT token has expired");
            throw e;
        } catch (UnsupportedJwtException e) {
            LOGGER.warn("Unsupported JWT token format");
            throw e;
        } catch (MalformedJwtException e) {
            LOGGER.warn("Malformed JWT token");
            throw e;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            LOGGER.warn("Invalid JWT signature");
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error parsing JWT token", e);
            throw new IllegalStateException("Failed to parse JWT token", e);
        }
    }

    private static void validateCreateParameters(String prefix, String key, JWTObject jwtObject) {
        if (isNullOrEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        if (isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (jwtObject == null) {
            throw new IllegalArgumentException("JWT object cannot be null");
        }
        if (isNullOrEmpty(jwtObject.getSubject())) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
    }

    private static void validateParseParameters(String token, String prefix, String key) {
        if (isNullOrEmpty(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (isNullOrEmpty(prefix)) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        if (isNullOrEmpty(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (!token.startsWith(prefix + " ")) {
            throw new IllegalArgumentException("Token does not start with expected prefix");
        }
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static SecretKey createSecretKey(String key) {
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    private static String sanitizeToken(String token, String prefix) {
        return token.replace(prefix + " ", "");
    }

    private static JWTObject buildJWTObject(Claims claims) {
        JWTObject object = new JWTObject();
        object.setSubject(claims.getSubject());
        object.setExpiration(claims.getExpiration());
        object.setIssuedAt(claims.getIssuedAt());

        Object authoritiesClaim = claims.get(ROLES_AUTHORITIES);
        List<Role> roles = extractRolesFromClaim(authoritiesClaim);
        object.setRoles(roles);

        return object;
    }

    private static List<String> processRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getName)
                .filter(Objects::nonNull)
                .filter(name -> !name.trim().isEmpty())
                .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .toList();
    }

    private static List<Role> extractRolesFromClaim(Object authoritiesClaim) {
        if (authoritiesClaim == null) {
            return Collections.emptyList();
        }

        if (!(authoritiesClaim instanceof List<?>)) {
            LOGGER.warn("Authorities claim is not a List, returning empty roles");
            return Collections.emptyList();
        }

        try {
            @SuppressWarnings("unchecked") // Safe cast after instanceof check
            List<String> roleStrings = (List<String>) authoritiesClaim;
            return convertStringListToRoles(roleStrings);
        } catch (ClassCastException e) {
            LOGGER.warn("Failed to cast authorities claim to List<String>, returning empty roles");
            return Collections.emptyList();
        }
    }

    private static List<Role> convertStringListToRoles(List<String> roleStrings) {
        if (roleStrings == null || roleStrings.isEmpty()) {
            return Collections.emptyList();
        }

        return roleStrings.stream()
                .filter(Objects::nonNull)
                .filter(roleString -> !roleString.trim().isEmpty())
                .map(JWTCreator::createRoleFromString)
                .toList();
    }

    private static Role createRoleFromString(String roleString) {
        String roleName = roleString.startsWith("ROLE_") ?
                roleString.substring(5) : roleString;

        Role role;
        try {
            role = Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unknown role name: {}", roleName);
            return null;
        }
        return role;
    }
}
