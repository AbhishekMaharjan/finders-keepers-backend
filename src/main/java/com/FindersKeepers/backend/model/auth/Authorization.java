package com.FindersKeepers.backend.model.auth;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "authorizations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Authorization implements Serializable {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "registered_client_id", length = 5)
    private String registeredClientId;

    @Column(name = "principal_name", length = 50)
    private String principalName;

    @Column(name = "authorization_grant_type", length = 25)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", length = 10)
    private String authorizedScopes;

    @Column(name = "attributes", length = 3500)
    private String attributes;

    @Column(name = "state", length = 500)
    private String state;

    @Column(name = "access_token_value", length = 700)
    private String accessTokenValue;

    @Column(name = "access_token_issued_at", length = 30)
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at", length = 30)
    private Instant accessTokenExpiresAt;

    @Column(name = "access_token_meta_data", length = 800)
    private String accessTokenMetadata;

    @Column(name = "access_token_type", length = 5)
    private String accessTokenType;

    @Column(name = "access_token_scopes", length = 5)
    private String accessTokenScopes;

    @Column(name = "refresh_token_value", length = 150)
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at", length = 30)
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at", length = 30)
    private Instant refreshTokenExpiresAt;

    @Column(name = "refresh_token_meta_data", length = 90)
    private String refreshTokenMetadata;

}