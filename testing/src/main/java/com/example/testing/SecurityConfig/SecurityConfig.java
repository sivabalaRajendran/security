package com.example.testing.SecurityConfig;


import com.example.testing.Filter.JwtAuthFilter;
import com.example.testing.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   @Autowired
    private JwtService jwtService;

    @Bean
    public JwtAuthFilter authFilter(AuthenticationManager authenticationManager) {
        return new JwtAuthFilter(jwtService, authenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API

                // Use the new .authorizeHttpRequests() method
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/register/**", "/api/verify-otp/**", "/api/login/**", "/api/resend-otp/**",
                                        "/api/change-password/**","/api/forgot-password/**").permitAll() // Public endpoints
                                .requestMatchers(  "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        "/swagger-resources/**",
                                        "/swagger-resources/configuration/ui",
                                        "/swagger-resources/configuration/security").permitAll()
                                .requestMatchers("/api/**").authenticated() // Secure endpoints
                )

                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session
                )

                .addFilterBefore(authFilter(http.getSharedObject(AuthenticationManager.class)), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(new JwtAuthenticationProvider(jwtService)) // Add the JwtAuthenticationProvider
                .build();
    }
}