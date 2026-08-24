package com.mmendoza.registerofusers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())

                //configuration of access
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/employee/**").hasAnyRole("EMPLOYEE", "ADMIN")
                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults())

                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123")) // Almacenamos la contraseña ya encriptada
                .roles("ADMIN")
                .build();

        UserDetails employee = User.withUsername("empleado")
                .password(encoder.encode("empleado123"))
                .roles("EMPLOYEE")
                .build();

        return new InMemoryUserDetailsManager(admin, employee); // Almacenamiento en memoria [4]
    }
}
