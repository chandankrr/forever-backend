package com.forever.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenHelper {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expires_in}")
    private int expiresIn;

    public String generateToken(String email) {
        return Jwts.builder()
                .issuer(appName)
                .subject(email)
                .issuedAt(new Date())
                .expiration(generateExpirationDate())
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + expiresIn * 1000L);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = getEmailFromToken(token);
        return (email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expireDate = getExpirationDate(token);
        return expireDate != null && expireDate.before(new Date());
    }

    private Date getExpirationDate(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            if (claims != null) {
                return claims.getExpiration();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getEmailFromToken(String authToken) throws JwtException {
        Claims claims = getAllClaimsFromToken(authToken);
        if (claims != null && claims.getSubject() != null) {
            return claims.getSubject();
        }
        throw new JwtException("Unable to get email from token");
    }

    private Claims getAllClaimsFromToken(String authToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
