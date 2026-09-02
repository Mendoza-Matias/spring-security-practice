package com.mmendoza.registerofusers.config;

import com.mmendoza.registerofusers.shared.error.ApiErrorResponseWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita anotaciones granulares como @PreAuthorize [9, 10]
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final CorsConfiguration corsConfiguration;
    private final ApiErrorResponseWriter errorResponseWriter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            CorsConfiguration corsConfiguration,
            ApiErrorResponseWriter errorResponseWriter
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfiguration = corsConfiguration;
        this.errorResponseWriter = errorResponseWriter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration.corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Manejo de errores seguro para APIs REST (Retorna JSON en lugar de redirección HTML) [6, 7]
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                errorResponseWriter.write(
                                        request,
                                        response,
                                        HttpStatus.UNAUTHORIZED,
                                        "UNAUTHORIZED",
                                        "Es necesario autenticarse para acceder a este recurso."
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                errorResponseWriter.write(
                                        request,
                                        response,
                                        HttpStatus.FORBIDDEN,
                                        "ACCESS_DENIED",
                                        "No tienes permisos para realizar esta operación."
                                )
                        )
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
