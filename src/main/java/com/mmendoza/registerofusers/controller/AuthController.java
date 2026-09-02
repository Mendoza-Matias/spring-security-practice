package com.mmendoza.registerofusers.controller;

import com.mmendoza.registerofusers.dto.auth.AuthResponse;
import com.mmendoza.registerofusers.dto.auth.LoginRequest;
import com.mmendoza.registerofusers.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE = "refresh_token";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response
    ) {
        AuthService.TokenPair tokens = authService.authenticate(
                loginRequest.username(),
                loginRequest.password()
        );

        addRefreshCookie(response, tokens);
        return ResponseEntity.ok(toResponse(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh token ausente");
        }

        try {
            AuthService.TokenPair tokens = authService.refresh(refreshToken);
            addRefreshCookie(response, tokens);
            return ResponseEntity.ok(toResponse(tokens));
        } catch (JwtException | IllegalArgumentException | AuthenticationException exception) {
            clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh token inválido", exception);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    private AuthResponse toResponse(AuthService.TokenPair tokens) {
        return new AuthResponse(tokens.accessToken(), "Bearer", tokens.accessExpiresIn());
    }

    private void addRefreshCookie(HttpServletResponse response, AuthService.TokenPair tokens) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofMillis(tokens.refreshExpiresIn()))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
