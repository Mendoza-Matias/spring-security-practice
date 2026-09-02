package com.mmendoza.registerofusers.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final CustomUserService customUserService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, CustomUserService customUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserService = customUserService;
    }

    public TokenPair authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return issueTokens(userDetails);
    }

    public TokenPair refresh(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh token inválido");
        }

        return issueTokens(userDetails);
    }

    private TokenPair issueTokens(UserDetails userDetails) {
        return new TokenPair(
                jwtService.generateAccessToken(userDetails),
                jwtService.generateRefreshToken(userDetails),
                jwtService.getAccessExpirationMs(),
                jwtService.getRefreshExpirationMs()
        );
    }

    public record TokenPair(
            String accessToken,
            String refreshToken,
            long accessExpiresIn,
            long refreshExpiresIn
    ) {
    }
}
