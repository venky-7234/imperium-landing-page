package com.imperium.ims.security;

import com.imperium.ims.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT token provider: handles generation, validation, and claims extraction.
 *
 * <p>Issues two token types:
 * <ul>
 *   <li><b>Access token</b> — short-lived (15 min default), carries user identity and roles.</li>
 *   <li><b>Refresh token</b> — long-lived (7 days default), used only to obtain new access tokens.</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry-ms}")
    private long accessTokenExpiryMs;

    @Value("${app.jwt.refresh-token-expiry-ms}")
    private long refreshTokenExpiryMs;

    // ── Token Generation ───────────────────────────────────────────────────

    /**
     * Generates a signed JWT access token for the authenticated principal.
     *
     * @param authentication the authenticated user from the security context
     * @return signed JWT string
     */
    public String generateAccessToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId())
                .claim("roles", roles)
                .claim("tokenType", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a signed JWT refresh token.
     *
     * @param authentication the authenticated user
     * @return signed JWT refresh token string
     */
    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId())
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Claims Extraction ──────────────────────────────────────────────────

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) parseClaims(token).get("roles");
    }

    // ── Validation ─────────────────────────────────────────────────────────

    /**
     * Validates a JWT token's signature and expiry.
     *
     * @param token the JWT string to validate
     * @return {@code true} if the token is valid
     * @throws InvalidTokenException on any validation failure
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new InvalidTokenException("JWT token has expired", ex);
        } catch (UnsupportedJwtException ex) {
            throw new InvalidTokenException("JWT token is unsupported", ex);
        } catch (MalformedJwtException ex) {
            throw new InvalidTokenException("JWT token is malformed", ex);
        } catch (SecurityException ex) {
            throw new InvalidTokenException("JWT signature validation failed", ex);
        } catch (IllegalArgumentException ex) {
            throw new InvalidTokenException("JWT token is empty or null", ex);
        }
    }

    // ── Private Helpers ────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
