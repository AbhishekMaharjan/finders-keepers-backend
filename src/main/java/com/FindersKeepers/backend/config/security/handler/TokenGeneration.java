package com.FindersKeepers.backend.config.security.handler;

import com.FindersKeepers.backend.config.security.utils.CustomPasswordUser;
import com.FindersKeepers.backend.repository.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.*;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.USER;


@Configuration
public class TokenGeneration {

    private final UserRepository userRepository;
    private final CustomMessageSource customMessageSource;
    @Value("${key.private}")
    private String privateKeyValue;
    @Value("${key.public}")
    private String publicKeyValue;

    public TokenGeneration(UserRepository userRepository, CustomMessageSource customMessageSource) {
        this.userRepository = userRepository;
        this.customMessageSource = customMessageSource;
    }

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(tokenCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            OAuth2ClientAuthenticationToken principal = context.getPrincipal();
            CustomPasswordUser user = (CustomPasswordUser) principal.getDetails();
            Map<String, Object> userMap = new HashMap<>();
            Optional<Users> optionalUsers = userRepository.findByEmail(user.username());
            if (optionalUsers.isEmpty())
                throw new NotFoundException(customMessageSource.get(NOT_FOUND, customMessageSource.get(USER)));
            Users users = optionalUsers.get();
            userMap.put("username", users.getEmail());
            userMap.put("id", users.getId());
            Set<String> authorities = user.authorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            if (context.getTokenType().getValue().equals("access_token")) {
                context.getClaims().claim("authorities", authorities)
                        .claim("user", userMap);
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private RSAKey generateRsa() {
        PublicKey publicKey = getPublicKey(publicKeyValue);
        PrivateKey privateKey = getPrivateKey(privateKeyValue);
        return new RSAKey.Builder((RSAPublicKey) publicKey).privateKey(privateKey).build();
    }

    private PrivateKey getPrivateKey(String privateKey) {
        try {
            String privateKeyPEM = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

            // Create a PKCS8EncodedKeySpec with the decoded key bytes
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            // Get a key factory instance for RSA and generate the private key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception ex) {
            throw new IllegalStateException(customMessageSource.get(ERROR_LOADING_PRIVATE_KEY), ex);
        }
    }

    private PublicKey getPublicKey(String publicKey) {
        try {
            String publicKeyPEM = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception ex) {
            throw new IllegalStateException(customMessageSource.get(ERROR_LOADING_PUBLIC_KEY), ex);
        }
    }
}
