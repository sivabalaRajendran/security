package com.example.testing.SecurityConfig;

import com.example.testing.Service.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("Invalid Token");
        }

        // Extract username from the token and validate it
        String username = jwtService.extractUsername(token);

        // Custom logic to verify token validity
        if (jwtService.isTokenExpired(token)) {
            throw new BadCredentialsException("Expired Token");
        }

        // Create the Authentication token with authorities (roles or permissions)
        return new UsernamePasswordAuthenticationToken(username, token, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
