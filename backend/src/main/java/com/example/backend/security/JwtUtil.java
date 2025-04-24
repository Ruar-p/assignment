package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
Utility class that handles all JWT operations:
- Generating tokens when users log in
- Extracting information from tokens
- Validating tokens when users access protected resources
 */

// TODO: In the future, a token refresh and revocation system could be added
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from token using a function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the token and extracts all of its claims
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey()) // Uses secret key to verify signature
                .build()
                .parseClaimsJws(token)          // Actually parse and validate the token
                .getBody();                     // Get the payload section
    }

    // Converts secret key string to the cryptographic key object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Could add custom claims here like roles: claims.put("role", "ADMIN")
        return createToken(claims, userDetails.getUsername());
    }

    // Does the actual JWT token creation
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .setClaims(claims)                                      // Additional data to include
                .setSubject(subject)                                    // Username
                .setIssuedAt(new Date(System.currentTimeMillis()))                      // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))    // Expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)    // Sign with secret key
                .compact(); // Generate actual JWT string
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
