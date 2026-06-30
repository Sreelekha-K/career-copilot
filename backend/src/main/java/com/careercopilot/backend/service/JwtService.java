package com.careercopilot.backend.service;

import com.careercopilot.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final long TOKEN_EXPIRATION_MILLIS = 1000L * 60 * 60 * 24;

    private final SecretKey signingKey;

    public JwtService() {
        this.signingKey = Keys.hmacShaKeyFor(resolveSecretBytes());
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        return Jwts.builder()
                .claims(Map.of(
                        "userId", user.getId(),
                        "name", user.getName()
                ))
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(TOKEN_EXPIRATION_MILLIS)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private byte[] resolveSecretBytes() {

        String secret = System.getenv("JWT_SECRET");

        if (secret != null && !secret.isBlank()) {
            try {
                byte[] decodedSecret = Decoders.BASE64.decode(secret);
                return decodedSecret.length >= 32 ? decodedSecret : sha256(secret);
            } catch (IllegalArgumentException ignored) {
                return sha256(secret);
            }
        }

        byte[] generatedSecret = new byte[64];
        new SecureRandom().nextBytes(generatedSecret);
        System.out.println("JWT_SECRET not configured. Generated temporary JWT signing key for this application run.");
        return generatedSecret;
    }

    private byte[] sha256(String value) {

        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available", ex);
        }
    }
}
