package com.uca.parcialfinalncapas.utils;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expiration-time}")
    private long expirationTime;

    public String generateToken(String email) {
        Key key = getSigningKey(); // Obtiene la clave HMAC desde .env

        return Jwts.builder()
                .subject(email) // Establece el email como "subject"
                .issuedAt(Date.from(Instant.now())) // Fecha de emisióPn
                .expiration(Date.from(Instant.now().plusMillis(expirationTime)))
                .signWith(key) // Firma el token con HMAC-SHA
                .compact(); // Convierte a String
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        SecretKey key = getSigningKey();
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}