package com.imperium.ims.service;

import com.imperium.ims.dto.*;

/**
 * Service contract for authentication operations.
 */
public interface AuthService {

    /**
     * Authenticates a user via Google OAuth 2.0 ID Token.
     *
     * @param request the google auth request containing the ID token
     * @return a response containing JWT tokens
     */
    LoginResponse googleLogin(GoogleAuthRequest request);

    /**
     * Issues a new access token using a valid refresh token.
     */
    LoginResponse refreshToken(RefreshTokenRequest request);

    /**
     * Invalidates the current user's session (token revocation via blacklist or DB flag).
     */
    void logout(String accessToken);

}
