package com.FindersKeepers.backend.config.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.function.Supplier;

public class DefaultAuthorizationServerContext implements AuthorizationServerContext {
        private final Supplier<String> issuerSupplier;
        @Getter
        private final AuthorizationServerSettings authorizationServerSettings;

        public DefaultAuthorizationServerContext(Supplier<String> issuerSupplier, AuthorizationServerSettings authorizationServerSettings) {
            this.issuerSupplier = issuerSupplier;
            this.authorizationServerSettings = authorizationServerSettings;
        }

        public String getIssuer() {
            return this.issuerSupplier.get();
        }

    public static String resolveIssuer(AuthorizationServerSettings authorizationServerSettings, HttpServletRequest request) {
        return authorizationServerSettings.getIssuer() != null ? authorizationServerSettings.getIssuer() : getContextPath(request);
    }

    public static String getContextPath(HttpServletRequest request) {
        return UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request)).replacePath(request.getContextPath()).replaceQuery((String) null).fragment((String) null).build().toUriString();
    }


}
