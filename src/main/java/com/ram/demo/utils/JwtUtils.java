package com.ram.demo.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()
            .stream().findFirst().map(GrantedAuthority::getAuthority).orElse(""));

        return Jwts.builder()
            .claims(claims)                                                     // replaces setClaims()
            .subject(userDetails.getUsername())                                 // replaces setSubject()
            .issuedAt(new Date())                                               // replaces setIssuedAt()
            .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // replaces setExpiration()
            .signWith(getSigningKey())                                          // no algorithm arg needed
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())          // replaces setSigningKey()
            .build()
            .parseSignedClaims(token)             // replaces parseClaimsJws()
            .getPayload();                        // replaces getBody()
    }

    private javax.crypto.SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}