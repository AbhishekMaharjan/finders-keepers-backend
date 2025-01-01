package com.FindersKeepers.backend.config.security.handler;


import com.FindersKeepers.backend.config.security.utils.DefaultAuthorizationServerContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenIntermediateHandler {

    public PasswordAuthenticationToken getPasswordAuthenticationToken(User user, RegisteredClient registeredClient) {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("username", user.getUsername());
        additionalInfo.put("password", user.getPassword());
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationTokens = new OAuth2ClientAuthenticationToken(registeredClient.getClientId(), ClientAuthenticationMethod.CLIENT_SECRET_BASIC, registeredClient.getClientSecret(), null);
        oAuth2ClientAuthenticationTokens.setAuthenticated(true);
        return new PasswordAuthenticationToken(oAuth2ClientAuthenticationTokens, null, additionalInfo);
    }

    public OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationTokenGenerator(User user, AuthorizationServerSettings authorizationServerSettings, HttpServletRequest httpServletRequest, PasswordAuthenticationProvider passwordAuthenticationProvider, PasswordAuthenticationToken passwordAuthenticationToken) {
        OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = passwordAuthenticationProvider.getClientPrincipal(passwordAuthenticationToken);
        AuthorizationServerContext authorizationServerContext = new DefaultAuthorizationServerContext(() -> DefaultAuthorizationServerContext.resolveIssuer(authorizationServerSettings, httpServletRequest), authorizationServerSettings);
        AuthorizationServerContextHolder.setContext(authorizationServerContext);
        passwordAuthenticationProvider.updateSecurityContext(user, oAuth2ClientAuthenticationToken);
        return oAuth2ClientAuthenticationToken;
    }

}
