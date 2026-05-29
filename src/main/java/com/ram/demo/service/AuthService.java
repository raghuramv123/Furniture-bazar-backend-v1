package com.ram.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ram.demo.dtos.AuthResponse;
import com.ram.demo.dtos.LoginRequest;
import com.ram.demo.dtos.RegisterRequest;
import com.ram.demo.entity.RefreshToken;
import com.ram.demo.entity.User;
import com.ram.demo.enums.Role;
import com.ram.demo.exception.EmailAlreadyExistsException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.RefreshTokenRepository;
import com.ram.demo.repository.UserRepository;
import com.ram.demo.security.UserDetailsServiceImpl;
import com.ram.demo.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${app.refresh-token.expiry-days:7}")
    private long refreshTokenExpiryDays;

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + req.getEmail());
        }

        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(Role.CUSTOMER)
                .active(true)
                .verified(false)
                // ── FIXED: set userName so NOT NULL constraint is satisfied ──
                //.userName(req.getEmail().toLowerCase())
             // use provided userName or fall back to email
                .userName(
                     req.getEmail().toLowerCase())
                .build();

        userRepository.save(user);
    }

    // ── LOGIN ─────────────────────────────────────────────
    public AuthResponse login(LoginRequest req) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());

        if (!passwordEncoder.matches(req.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = createRefreshToken(req.getEmail());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900L)
            .role(userDetails.getAuthorities().stream()
                .findFirst().map(GrantedAuthority::getAuthority).orElse(""))
            .email(req.getEmail())
            .build();
    }

    // ── REFRESH TOKEN ─────────────────────────────────────
    public AuthResponse refreshAccessToken(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() ->
            new IllegalArgumentException("dlfjldjfl")
//            new InvalidTokenException("Refresh token not found")
            );

        if (stored.isRevoked() || stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored);
//            throw new InvalidTokenException("Refresh token expired or revoked");
            throw new IllegalArgumentException();
        }

        UserDetails userDetails = userDetailsService
            .loadUserByUsername(stored.getUser().getEmail());

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken) // reuse same refresh token
            .tokenType("Bearer")
            .expiresIn(900l)
            .build();
    }

    public void logout(String refreshToken) {
        // ── FIXED: delete token on logout instead of marking revoked ──
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
    private String createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ── FIXED: delete old token completely instead of marking revoked ──
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        // flush to make sure delete is committed before insert
        refreshTokenRepository.flush();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusDays(refreshTokenExpiryDays));
        token.setRevoked(false);

        return refreshTokenRepository.save(token).getToken();
    }
    
    
    
}