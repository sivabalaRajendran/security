package com.example.testing.Filter;

import com.example.testing.Service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtAuthFilter(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);

                // Create Authentication object with token
                Authentication authentication = new UsernamePasswordAuthenticationToken(token, token);

                // Authenticate with the manager and set context
                authentication = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // If there's an error, set unauthorized response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> map = new HashMap<>();
            map.put("Message", "Invalid Token");
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(map);
            response.setContentType("application/json");
            response.getWriter().write(json);
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        	 try {
//        		 UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            if (jwtService.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }} catch (Exception exception) {
//                 System.out.println(exception.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 Map<String, String> map = new HashMap<>();
//                 map.put("Message","InValid Token");
//                 ObjectMapper objectMapper = new ObjectMapper();
//                 String json = objectMapper.writeValueAsString(map);
//                 response.setContentType("application/json");
//                 response.getWriter().write(json);
//             }
//        }
//        filterChain.doFilter(request, response);
//





//
//import com.example.testing.Service.JwtService;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    // Constructor to inject JwtService
//    public JwtAuthFilter(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String token = request.getHeader("Authorization");
//
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);  // Extract the token after "Bearer "
//
//            try {
//                // Extract claims from the token
//                Claims claims = jwtService.extractClaims(token);

                // Extract the email from the token
//                String email = claims.getSubject();
//
//                // Validate the token and set authentication if valid
//                if (email != null && jwtService.validateToken(token, email)) {
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(email, null, null);
//          SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            } catch (Exception e) {
//                // Handle exceptions, like expired token or invalid signature
//                System.out.println("JWT Token parsing failed: " + e.getMessage());
//            }
//        }
//
//        filterChain.doFilter(request, response);  // Continue the filter chain
//    }
//}