package com.FindersKeepers.backend.config.security.handler;


import com.FindersKeepers.backend.config.security.jpa.JpaOAuth2AuthorizationService;
import com.FindersKeepers.backend.config.security.user.LimitValidatorThreadLocalStorage;
import com.FindersKeepers.backend.config.security.user.MyUserDetailsService;
import com.FindersKeepers.backend.config.security.user.RegisteredClientIdThreadLocalStorage;
import com.FindersKeepers.backend.config.security.utils.CustomPasswordUser;
import com.FindersKeepers.backend.config.security.utils.IntermediateHandler;
import com.FindersKeepers.backend.config.security.utils.TokenValidityStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
public class PasswordAuthenticationProvider implements AuthenticationProvider {
    private final IntermediateHandler intermediateHandler;
    private final JpaOAuth2AuthorizationService authorizationService;
    private final MyUserDetailsService userDetailsService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public PasswordAuthenticationProvider(IntermediateHandler intermediateHandler, JpaOAuth2AuthorizationService authorizationService,
                                          OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
                                          MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.intermediateHandler = intermediateHandler;
        this.passwordEncoder = passwordEncoder;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
        this.userDetailsService = userDetailsService;
    }

    protected OAuth2ClientAuthenticationToken getClientPrincipal(PasswordAuthenticationToken passwordAuthenticationToken) {
        return intermediateHandler.getAuthenticatedClientElseThrowInvalidClient(passwordAuthenticationToken);
    }

    protected PasswordAuthenticationToken getPasswordAuthenticationToken(Authentication authentication) {
        return (PasswordAuthenticationToken) authentication;
    }

    protected Set<String> getAuthorizedScopes(User user, RegisteredClient registeredClient) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(scope -> {
                    assert registeredClient != null;
                    return registeredClient.getScopes().contains(scope);
                })
                .collect(Collectors.toSet());
    }

    protected User getAuthenticatedUser(String username, String registeredClientId) {
        RegisteredClientIdThreadLocalStorage.setId(registeredClientId);
        User users = userDetailsService.loadUserByUsername(username);
        RegisteredClientIdThreadLocalStorage.clearId();
        return users;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws BadCredentialsException {
        Map<String, Object> additionalParameters = new HashMap<>();
        PasswordAuthenticationToken passwordAuthenticationToken = getPasswordAuthenticationToken(authentication);
        OAuth2ClientAuthenticationToken clientPrincipal = getClientPrincipal(passwordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
        assert registeredClient != null;
        String username = passwordAuthenticationToken.getUsername();
        String password = passwordAuthenticationToken.getPassword();
        additionalParameters.put("username", username);
        additionalParameters.put("registered_client_id", registeredClient.getId());
        User user;
        try {
            LimitValidatorThreadLocalStorage.setStatus(true);
            user = getAuthenticatedUser(username, registeredClient.getId());
            LimitValidatorThreadLocalStorage.clearStatus();
            additionalParameters.put("role", user.getAuthorities());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException(OAuth2ErrorCodes.ACCESS_DENIED);
        }
        if (!passwordEncoder.matches(password, user.getPassword()) || !user.getUsername().equals(username)) {
            throw new BadCredentialsException(OAuth2ErrorCodes.ACCESS_DENIED);
        }

        Set<String> authorizedScopes = getAuthorizedScopes(user, registeredClient);

        //-----------Create a new Security Context Holder Context----------
        updateSecurityContext(user, null);

        //-----------FIND TOKEN----------------------
        OAuth2Authorization oAuth2Authorization = authorizationService.findByUsernameAndRegisteredClientId(username, registeredClient.getId());
        OAuth2Authorization.Builder authorizationBuilder = authorizationBuilder(registeredClient, clientPrincipal, authorizedScopes, passwordAuthenticationToken);
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = tokenContextBuilder(registeredClient, clientPrincipal, authorizedScopes, passwordAuthenticationToken);

        if (oAuth2Authorization != null) {
            TokenValidityStatus tokenValidityStatus = authorizationService.validateAccessToken(username, registeredClient.getId());
            if (tokenValidityStatus.equals(TokenValidityStatus.REFRESH_ACCESS_TOKEN)) {
                OAuth2AccessToken oAuth2AccessToken = generateAccessToken(authorizationBuilder, tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build());
                authorizationService.refreshAccessToken(username, oAuth2AccessToken, registeredClient.getId());
                return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, oAuth2AccessToken, Objects.requireNonNull(oAuth2Authorization.getRefreshToken()).getToken(), additionalParameters);
            } else if (tokenValidityStatus.equals(TokenValidityStatus.EXISTING)) {
                return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, Objects.requireNonNull(oAuth2Authorization.getAccessToken()).getToken(), Objects.requireNonNull(oAuth2Authorization.getRefreshToken()).getToken(), additionalParameters);
            }
        }

        //-----------FAILED TO FIND ACCESS TOKEN------------
        return tokenBuilder(tokenContextBuilder, authorizationBuilder, registeredClient, clientPrincipal, additionalParameters);

    }

    protected void updateSecurityContext(User user, OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken) {
        if (oAuth2ClientAuthenticationToken == null)
            oAuth2ClientAuthenticationToken = (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        CustomPasswordUser customPasswordUser = new CustomPasswordUser(user.getUsername(), user.getAuthorities());
        oAuth2ClientAuthenticationToken.setDetails(customPasswordUser);

        var newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(oAuth2ClientAuthenticationToken);
        SecurityContextHolder.setContext(newContext);
    }


    protected OAuth2AccessToken generateAccessToken(OAuth2Authorization.Builder authorizationBuilder, OAuth2TokenContext tokenContextBuilder) {
        //-----------ACCESS TOKEN----------
        OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContextBuilder);
        if (generatedAccessToken == null) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                    "The token generator failed to generate the access token.", "");
            throw new OAuth2AuthenticationException(error);
        }

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                generatedAccessToken.getExpiresAt(), tokenContextBuilder.getAuthorizedScopes());


        if (generatedAccessToken instanceof ClaimAccessor claimAccessor) {
            authorizationBuilder.token(accessToken, metadata ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, (claimAccessor).getClaims()));
        } else {
            authorizationBuilder.accessToken(accessToken);
        }
        return accessToken;
    }


    protected DefaultOAuth2TokenContext.Builder tokenContextBuilder(RegisteredClient registeredClient, OAuth2ClientAuthenticationToken clientPrincipal, Set<String> authorizedScopes, PasswordAuthenticationToken passwordAuthenticationToken) {
        return DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(clientPrincipal)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(authorizedScopes)
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .authorizationGrant(passwordAuthenticationToken);
    }

    protected OAuth2Authorization.Builder authorizationBuilder(RegisteredClient registeredClient, OAuth2ClientAuthenticationToken clientPrincipal, Set<String> authorizedScopes, PasswordAuthenticationToken passwordAuthenticationToken) {
        return OAuth2Authorization.withRegisteredClient(registeredClient)
                .attribute(Principal.class.getName(), clientPrincipal)
                .principalName(passwordAuthenticationToken.getUsername())
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .authorizedScopes(authorizedScopes);
    }

    public OAuth2AccessTokenAuthenticationToken tokenBuilder(DefaultOAuth2TokenContext.Builder tokenContextBuilder, OAuth2Authorization.Builder authorizationBuilder, RegisteredClient registeredClient, OAuth2ClientAuthenticationToken clientPrincipal, Map<String, Object> additionalParam) {

        //-----------ACCESS TOKEN----------
        OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2AccessToken accessToken = generateAccessToken(authorizationBuilder, tokenContext);

        //-----------REFRESH TOKEN----------
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

            tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the refresh token.", "");
                throw new OAuth2AuthenticationException(error);
            }
            refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
            authorizationBuilder.refreshToken(refreshToken);
        }

        OAuth2Authorization authorization = authorizationBuilder.build();
        this.authorizationService.save(authorization);

        if (Objects.nonNull(additionalParam))
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, additionalParam);
        else
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}