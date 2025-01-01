package com.FindersKeepers.backend.config.security.handler;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.config.security.jpa.JpaOAuth2AuthorizationService;
import com.FindersKeepers.backend.config.security.jpa.JpaRegisteredClientRepository;
import com.FindersKeepers.backend.config.security.user.LimitValidatorThreadLocalStorage;
import com.FindersKeepers.backend.pojo.util.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;

import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.*;


@Configuration
@RequiredArgsConstructor
public class RefreshTokenHandler {
    private final JpaOAuth2AuthorizationService jpaOAuth2AuthorizationService;
    private final PasswordAuthenticationProvider passwordAuthenticationProvider;
    private final JpaRegisteredClientRepository jpaRegisteredClientRepository;
    private final AuthorizationServerSettings authorizationServerSettings;
    private final PasswordEncoder passwordEncoder;
    private final CustomMessageSource customMessageSource;
    private final TokenIntermediateHandler intermediateHandler;

    private static String[] extractBasicDetails(String basicAuthString) {
        // Extract the encoded part after "Basic "
        String encodedCredentials = basicAuthString.split(" ")[1];
        byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
        String decryptedString = new String(decodedBytes);
        return decryptedString.split(":");
    }

    private OAuth2Authorization findToken(String refreshToken) {
        return jpaOAuth2AuthorizationService.findByToken(refreshToken, OAuth2TokenType.REFRESH_TOKEN);
    }

    private boolean validateClientDetails(String[] details, RegisteredClient registeredClient) {
        String client = details[0];
        String secret = details[1];
        return registeredClient.getClientId().equals(client) && passwordEncoder.matches(secret, registeredClient.getClientSecret());
    }


    public TokenResponse generateAccessToken(String refreshToken, HttpServletRequest httpServletRequest, String authorization) {
        OAuth2Authorization oAuth2Authorization = findToken(refreshToken);
        if (Objects.isNull(oAuth2Authorization))
            throw new AuthenticationCredentialsNotFoundException(customMessageSource.get(INVALID_REFRESH_TOKEN));

        //Validation Of expiry of Refresh Token
        if (Instant.now().isAfter(Objects.requireNonNull(Objects.requireNonNull(oAuth2Authorization.getRefreshToken()).getToken().getExpiresAt())))
            throw new CredentialsExpiredException(customMessageSource.get(EXPIRED_REFRESH_TOKEN));

        RegisteredClient registeredClient = jpaRegisteredClientRepository.findById(oAuth2Authorization.getRegisteredClientId());
        assert registeredClient != null;
        if (!validateClientDetails(extractBasicDetails(authorization), registeredClient))
            throw new AuthenticationCredentialsNotFoundException(customMessageSource.get(ERROR_INVALID_CLIENT_CREDENTIAL));

        LimitValidatorThreadLocalStorage.setStatus(false);
        User user = passwordAuthenticationProvider.getAuthenticatedUser(oAuth2Authorization.getPrincipalName(), registeredClient.getId());
        LimitValidatorThreadLocalStorage.clearStatus();

        Set<String> authorizedScopes = passwordAuthenticationProvider.getAuthorizedScopes(user, registeredClient);

        PasswordAuthenticationToken passwordAuthenticationToken = intermediateHandler.getPasswordAuthenticationToken(user, registeredClient);
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = intermediateHandler.oAuth2ClientAuthenticationTokenGenerator(user, authorizationServerSettings, httpServletRequest, passwordAuthenticationProvider, passwordAuthenticationToken);
        DefaultOAuth2TokenContext tokenContext = passwordAuthenticationProvider.tokenContextBuilder(registeredClient, oAuth2ClientAuthenticationToken, authorizedScopes, passwordAuthenticationToken).tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Authorization.Builder authorizationBuilder = passwordAuthenticationProvider.authorizationBuilder(registeredClient, oAuth2ClientAuthenticationToken, authorizedScopes, passwordAuthenticationToken);

        OAuth2AccessToken oAuth2AccessToken = passwordAuthenticationProvider.generateAccessToken(authorizationBuilder, tokenContext);

        jpaOAuth2AuthorizationService.refreshAccessToken(oAuth2Authorization.getPrincipalName(), oAuth2AccessToken, registeredClient.getId());

        return generateResponse(oAuth2AccessToken, refreshToken);

    }

    private TokenResponse generateResponse(OAuth2AccessToken oAuth2AccessToken, String refreshToken) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(oAuth2AccessToken.getTokenValue());
        tokenResponse.setRefresh_token(refreshToken);
        tokenResponse.setExpires_in(Objects.requireNonNull(oAuth2AccessToken.getExpiresAt()).getEpochSecond() - Objects.requireNonNull(oAuth2AccessToken.getIssuedAt()).getEpochSecond());
        return tokenResponse;
    }


}
