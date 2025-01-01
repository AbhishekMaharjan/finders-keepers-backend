package com.FindersKeepers.backend.config.security.jpa;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.config.resources.Serializable.LongMixin;
import com.FindersKeepers.backend.config.security.utils.IntermediateHandler;
import com.FindersKeepers.backend.config.security.utils.TokenValidityStatus;
import com.FindersKeepers.backend.model.auth.Authorization;
import com.FindersKeepers.backend.repository.AuthorizationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_VALIDATION;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ACCESS_TOKEN;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.REFRESH_TOKEN;

@Service
public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {
    private final AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final IntermediateHandler intermediateHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CustomMessageSource customMessageSource;

    public JpaOAuth2AuthorizationService(AuthorizationRepository authorizationRepository, RegisteredClientRepository registeredClientRepository, IntermediateHandler intermediateHandler, CustomMessageSource customMessageSource) {
        this.intermediateHandler = intermediateHandler;
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
        this.customMessageSource = customMessageSource;

        ClassLoader classLoader = JpaOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setMixInAnnotation(Long.class, LongMixin.class);
        this.objectMapper.registerModule(simpleModule);
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        this.authorizationRepository.save(toEntity(authorization));
    }


    public OAuth2Authorization findByUsernameAndRegisteredClientId(String username, String registeredClientId) {
        Optional<Authorization> result = authorizationRepository.findByPrincipalNameAndRegisteredClientId(username, registeredClientId);
        return result.map(this::toObject).orElse(null);
    }

    public TokenValidityStatus validateAccessToken(String username, String registeredClientId) {
        Optional<Authorization> result = authorizationRepository.findByPrincipalNameAndRegisteredClientId(username, registeredClientId);
        if (result.isEmpty())
            throw new OAuth2AuthenticationException(customMessageSource.get(ERROR_VALIDATION, customMessageSource.get(ACCESS_TOKEN)));
        Authorization authorization = result.get();
        OAuth2Authorization oAuth2Authorization = toObject(authorization);
        if (Instant.now().isAfter(authorization.getAccessTokenExpiresAt())) {
            if (validateRefreshToken(authorization)) {
                remove(oAuth2Authorization);
                return TokenValidityStatus.CREATE_ALL;
            } else
                return TokenValidityStatus.REFRESH_ACCESS_TOKEN;
        }
        return TokenValidityStatus.EXISTING;
    }

    public boolean validateRefreshToken(Authorization authorization) {
        return Instant.now().isAfter(authorization.getRefreshTokenExpiresAt());
    }

    public void refreshAccessToken(String username, OAuth2AccessToken oAuth2AccessToken, String registeredClientId) {
        Optional<Authorization> result = authorizationRepository.findByPrincipalNameAndRegisteredClientId(username, registeredClientId);
        if (result.isEmpty())
            throw new OAuth2AuthenticationException(customMessageSource.get(ERROR_VALIDATION, customMessageSource.get(REFRESH_TOKEN)));
        Authorization authorization = result.get();
        authorization.setAccessTokenValue(oAuth2AccessToken.getTokenValue());
        authorization.setAccessTokenIssuedAt(oAuth2AccessToken.getIssuedAt());
        authorization.setAccessTokenExpiresAt(oAuth2AccessToken.getExpiresAt());
        authorizationRepository.save(authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        this.authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return this.authorizationRepository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String attributes, OAuth2TokenType tokenType) {

        Optional<Authorization> result;
        if (tokenType == null) {
            result = this.authorizationRepository.findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(attributes);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByAccessTokenValue(attributes);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByRefreshTokenValue(attributes);
        } else {
            result = Optional.empty();
        }

        return result.map(this::toObject).orElse(null);
    }


    private OAuth2Authorization toObject(Authorization entity) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(intermediateHandler.resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(StringUtils.commaDelimitedListToSet(entity.getAuthorizedScopes()));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(),
                    StringUtils.commaDelimitedListToSet(entity.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(),
                    entity.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshTokenMetadata())));
        }

        return builder.build();
    }

    private Authorization toEntity(OAuth2Authorization authorization) {
        Authorization entity = new Authorization();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAuthorizedScopes(StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ","));
        entity.setAttributes(writeMap(authorization.getAttributes()));
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        setTokenValues(
                accessToken,
                entity::setAccessTokenValue,
                entity::setAccessTokenIssuedAt,
                entity::setAccessTokenExpiresAt,
                entity::setAccessTokenMetadata
        );
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ","));
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        setTokenValues(
                refreshToken,
                entity::setRefreshTokenValue,
                entity::setRefreshTokenIssuedAt,
                entity::setRefreshTokenExpiresAt,
                entity::setRefreshTokenMetadata
        );

        return entity;
    }

    private void setTokenValues(
            OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer,
            Consumer<Instant> issuedAtConsumer,
            Consumer<Instant> expiresAtConsumer,
            Consumer<String> metadataConsumer) {
        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
            expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> metadata) {
        try {
            return this.objectMapper.writeValueAsString(metadata);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }


}