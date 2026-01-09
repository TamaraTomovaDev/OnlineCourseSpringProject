package org.intecbrussel.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
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

    public JwtUtil(Key key) {
        this.key = key;
    }

    /**
     * Genereert JWT token met username en role claim
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationHours * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    /**
     * Valideert token. Gooi exception bij verlopen of ongeldig token.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token expired");
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
    }

    /**
     * Haalt username uit token
     */
    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token expired");
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
    }

    /**
     * Haalt role uit token
     */
    public String getRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token expired");
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
    }
}
