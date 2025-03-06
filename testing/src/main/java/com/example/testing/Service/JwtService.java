package com.example.testing.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    private static String SECRETS;

    @Value("${jwt.secret}")
    private String SECRET;
    // @Value("${jwt.expires_in}")
    private long EXPIRES_IN = 3155695200000L;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Token received!");
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            throw new BadCredentialsException("Expired Token received!");
        }
    }

    public String generateToken(String email, String mobileNumber, Integer organizationKeyId, Short organizationType, Long UserKeyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("organizationType", organizationType);
        claims.put("organizationKeyId", organizationKeyId);
        claims.put("email", email);
        claims.put("UserKeyId", UserKeyId);
        return createToken(claims, email, mobileNumber);
    }

    private String createToken(Map<String, Object> claims, String email, String mobileNumber) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuer("chataak")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(generateExpirationDate())
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    private String createToken(Map<String, Object> claims, String email, String mobileNumber, Date expirationDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuer("chataak")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate) // Set the expiration date here
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, "", "", new Date(0)); // Token with expiration in the past
    }

    private List<String> revokedTokens = new ArrayList<>();

    public void revokeToken(String token) {
        revokedTokens.add(token);
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenRevoked(token));
    }


    private Boolean isTokenRevoked(String token) {
        return revokedTokens.contains(token);
    }


    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.EXPIRES_IN * 10000);
    }


    public Map<String, Object> extractClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

}






/*
    private static final String SECRET_KEY = "d025b8fc93e7438ae3577bfccf1a8f562b90d40e8e616536d707802733b11c4e";  // Use a stronger key in production

    // Method to generate JWT Token
    public String generateToken(String email, String phoneNumber, int organizationId, short organizationType, Long organizationKeyId) {
        return Jwts.builder()
                .setSubject(email)  // Store the email as the subject (you can add more claims as needed)
                .claim("phone", phoneNumber)
                .claim("organizationId", organizationId)
                .claim("organizationType", organizationType)
                .setIssuedAt(new Date())  // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // Set the expiration date (1 hour)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Sign the token with the secret key
                .compact();
    }
    // Method to extract Claims from JWT token
   public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to validate the JWT token
    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }
    // Method to validate token
    public boolean validateToken(String token, String email) {
        String tokenEmail = extractClaims(token).getSubject();
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }*/



