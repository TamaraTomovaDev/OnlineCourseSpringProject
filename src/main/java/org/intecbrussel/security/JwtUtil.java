package org.intecbrussel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Nonnull;
import org.intecbrussel.exception.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    @Value("${jwt.expiration-hours}")
    private long expirationHours;

    public JwtUtil(Key jwtSigningKey) {
        this.key = jwtSigningKey;
    }

    public String generateToken(String username, String role) {
        long expMs = expirationHours * 60 * 60 * 1000;

        return Jwts.builder()
                .setSubject(username)          // ✅ sub
                .claim("role", role)           // ✅ role claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expMs))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(@Nonnull String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid or expired JWT token");
        }
    }

    public boolean validateToken(String token) {
        parseClaims(token); // throws if invalid
        return true;
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
