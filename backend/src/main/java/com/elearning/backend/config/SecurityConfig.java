package com.elearning.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ===== PUBLIC — no login needed =====

                        // Auth endpoints
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Home page data
                        .requestMatchers("/api/home/**")
                        .permitAll()

                        // Stripe webhook
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payment/webhook")
                        .permitAll()

                        // Browse all courses
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/student/courses")
                        .permitAll()

                        // Single course detail
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/student/courses/*")
                        .permitAll()

                        // Certificate verification — public
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/certificate/verify/*")
                        .permitAll()

                        // ===== ADMIN ONLY =====
                        .requestMatchers("/api/admin/**")
                        .hasAuthority("ROLE_ADMIN")

                        // ===== INSTRUCTOR + ADMIN =====
                        .requestMatchers("/api/instructor/**")
                        .hasAnyAuthority(
                                "ROLE_INSTRUCTOR",
                                "ROLE_ADMIN")

                        // ===== ALL LOGGED IN USERS =====
                        .requestMatchers("/api/student/**")
                        .hasAnyAuthority(
                                "ROLE_STUDENT",
                                "ROLE_INSTRUCTOR",
                                "ROLE_ADMIN")

                        .requestMatchers("/api/payment/**")
                        .hasAnyAuthority(
                                "ROLE_STUDENT",
                                "ROLE_INSTRUCTOR",
                                "ROLE_ADMIN")

                        .requestMatchers("/api/certificate/**")
                        .hasAnyAuthority(
                                "ROLE_STUDENT",
                                "ROLE_INSTRUCTOR",
                                "ROLE_ADMIN")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:4200"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT",
                "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}