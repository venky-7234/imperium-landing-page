package com.imperium.ims.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.imperium.ims.service.CustomUserDetailsService;
/**
 * Spring Security configuration.
 *
 * <p>Key decisions:
 * <ul>
 *   <li>Stateless JWT — sessions are never created ({@link SessionCreationPolicy#STATELESS}).</li>
 *   <li>CSRF disabled — not needed for stateless REST APIs.</li>
 *   <li>Method-level security enabled via {@link EnableMethodSecurity} for
 *       {@code @PreAuthorize} / {@code @PostAuthorize} on service methods.</li>
 *   <li>Public routes are explicitly whitelisted; everything else requires auth.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CorsConfigurationSource corsConfigurationSource;

    // ── Public Endpoints ───────────────────────────────────────────────────

    private static final String[] PUBLIC_URLS = {
            // Google Login Endpoint
            "/auth/google",
            "/auth/refresh", // Keep refresh token endpoint public
            // Swagger / OpenAPI
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            // Actuator health
            "/actuator/health",
            "/actuator/info",
            // Landing Page / Public APIs
            "/invitations/rsvp/**",
            "/invitations/public/**"
    };

    // ── Security Filter Chain ──────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // CSRF — disabled for stateless REST
            .csrf(AbstractHttpConfigurer::disable)
            // Session — stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Auth entry point
            .exceptionHandling(ex ->
                    ex.authenticationEntryPoint(authenticationEntryPoint))
            // Route-level authorization
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    .requestMatchers(HttpMethod.POST, "/applications").permitAll() // Landing page API
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/superadmin/**", "/super-admin/**").hasRole("SUPER_ADMIN")
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .requestMatchers("/users/**").authenticated()
                    .anyRequest().authenticated()
            )
            // JWT filter runs before the username/password filter
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Beans ──────────────────────────────────────────────────────────────

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
