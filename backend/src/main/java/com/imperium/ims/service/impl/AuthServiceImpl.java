package com.imperium.ims.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.imperium.ims.dto.*;
import com.imperium.ims.entity.LoginHistory;
import com.imperium.ims.entity.RefreshToken;
import com.imperium.ims.repository.LoginHistoryRepository;
import com.imperium.ims.repository.RefreshTokenRepository;
import com.imperium.ims.service.AuthService;
import com.imperium.ims.service.AuditService;
import com.imperium.ims.exception.BusinessException;
import com.imperium.ims.exception.InvalidTokenException;
import com.imperium.ims.security.JwtTokenProvider;
import com.imperium.ims.security.UserPrincipal;
import com.imperium.ims.entity.Role;
import com.imperium.ims.entity.User;
import com.imperium.ims.repository.RoleRepository;
import com.imperium.ims.repository.UserRepository;
import com.imperium.ims.common.enums.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;
    private final LoginHistoryRepository loginHistoryRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           JwtTokenProvider tokenProvider,
                           AuditService auditService,
                           LoginHistoryRepository loginHistoryRepository,
                           @Value("${google.client.id}") String googleClientId) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
        this.loginHistoryRepository = loginHistoryRepository;
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    @Override
    public LoginResponse googleLogin(GoogleAuthRequest request) {
        String email;
        String googleId;
        String name;
        String familyName;
        String givenName;
        String pictureUrl;
        
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(request.getIdToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                email = payload.getEmail();
                googleId = payload.getSubject(); // Google's unique user ID
                name = (String) payload.get("name");
                familyName = (String) payload.get("family_name");
                givenName = (String) payload.get("given_name");
                pictureUrl = (String) payload.get("picture");
            } else {
                throw new BusinessException("Invalid ID token.");
            }
        } catch (Exception e) {
            log.error("Google Token Verification Failed", e);
            throw new BusinessException("Google token verification failed.");
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> {
            log.warn("Login attempt by unregistered email: {}", email);
            return new BusinessException("Access Denied: Your Gmail address is not registered in the system.");
        });

        if (user.getStatus() != Status.ACTIVE) {
            throw new BusinessException("Account is not active.");
        }

        boolean isAuthorized = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SUPER_ADMIN") || role.getName().equals("ADMIN") || role.getName().equals("USER"));

        if (!isAuthorized) {
            throw new BusinessException("Access Denied: Unauthorized role. Guests cannot authenticate.");
        }
        
        // Sync Google attributes to MySQL if missing/updated
        boolean updated = false;
        if (googleId != null && !googleId.equals(user.getGoogleId())) {
            user.setGoogleId(googleId);
            updated = true;
        }
        if (pictureUrl != null && !pictureUrl.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(pictureUrl);
            updated = true;
        }
        if (givenName != null && !givenName.equals(user.getFirstName())) {
            user.setFirstName(givenName);
            updated = true;
        }
        if (familyName != null && !familyName.equals(user.getLastName())) {
            user.setLastName(familyName);
            updated = true;
        }
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Authenticate
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserPrincipal.of(
                        user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                        user.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList(), user.isEnabled()
                ), null, user.getRoles().stream().map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.getName())).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Delete old refresh tokens for single session
        refreshTokenRepository.deleteByUserId(user.getId());

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenString = tokenProvider.generateRefreshToken(authentication);

        long expiryDays = 7;
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenString)
                .expiryDate(LocalDateTime.now().plusDays(expiryDays))
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> roles = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .toList();

        auditService.log(user.getUsername(), "LOGIN", "USER", user.getId().toString(), "User logged in via Google successfully", "SUCCESS");

        String ipAddress = "UNKNOWN";
        String userAgent = "UNKNOWN";
        try {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ipAddress = req.getRemoteAddr();
            userAgent = req.getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("Could not extract request details for login history");
        }

        LoginHistory history = LoginHistory.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status("SUCCESS")
                .loginTime(LocalDateTime.now())
                .build();
        loginHistoryRepository.save(history);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        if (user.getStatus() != Status.ACTIVE) {
            throw new BusinessException("Account is no longer active");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserPrincipal.of(
                        user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
                        user.getRoles().stream().map(r -> "ROLE_" + r.getName()).toList(), user.isEnabled()
                ), null, user.getRoles().stream().map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r.getName())).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = tokenProvider.generateAccessToken(authentication);

        List<String> roles = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .toList();

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            Long userId = tokenProvider.getUserIdFromToken(accessToken);
            refreshTokenRepository.deleteByUserId(userId);
            auditService.log("User_" + userId, "LOGOUT", "USER", userId.toString(), "User logged out manually", "SUCCESS");
        }
    }
}
