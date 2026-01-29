package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // List of public paths
    private static final String[] PUBLIC_PATHS = {
            "/",
            "/favicon.ico",
            "/robots.txt",
            "/.well-known/**",
            "/error",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**"
    };

    // List of public API paths
    private static final String[] PUBLIC_API_PATHS = {
            "/api/auth/**",
            "/api/manage/health"
    };

    // List of Swagger/OpenAPI paths
    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/swagger-ui*/*swagger-initializer.js",
            "/swagger-ui*/**"
    };

    // List of Actuator paths (can be restricted if needed)
    private static final String[] ACTUATOR_PATHS = {
            "/actuator/health",
            "/actuator/info"
    };

    // List of H2 Console paths (development only!)
    private static final String[] H2_CONSOLE_PATHS = {
            "/h2-console/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure authentication exception handling
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))

                // Configure sessions as STATELESS (stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure request authorization - all public paths in one place
                .authorizeHttpRequests(auth -> auth
                        // Static and service files
                        .requestMatchers(PUBLIC_PATHS).permitAll()

                        // Authentication API and health checks
                        .requestMatchers(PUBLIC_API_PATHS).permitAll()

                        // Swagger UI and documentation
                        .requestMatchers(SWAGGER_PATHS).permitAll()

                        // Actuator endpoints
                        .requestMatchers(ACTUATOR_PATHS).permitAll()

                        // H2 Console (development)
                        .requestMatchers(H2_CONSOLE_PATHS).permitAll()

                        // OPTIONS requests for CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // For H2 Console in development mode
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        // Add JWT filter before standard authentication filter
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",  // React application
                "http://localhost:4200",  // Angular application
                "http://localhost:8080"   // Current server
        ));

        // Allow methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allow headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Allow credentials (cookies, authorization)
        configuration.setAllowCredentials(true);

        // Pre-flight request lifetime in seconds
        configuration.setMaxAge(3600L);

        // Headers that can be exposed to the client
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Disposition"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}