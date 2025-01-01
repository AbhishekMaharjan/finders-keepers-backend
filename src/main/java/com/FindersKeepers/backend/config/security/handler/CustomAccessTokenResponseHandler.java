package com.FindersKeepers.backend.config.security.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CustomAccessTokenResponseHandler implements AuthenticationSuccessHandler {

    private final HttpMessageConverter<OAuth2AccessTokenResponse> httpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;
        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                        .tokenType(accessToken.getTokenType())
                        .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(Instant.now(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        httpMessageConverter.write(accessTokenResponse, null, httpResponse);
    }

}