package com.FindersKeepers.backend.model.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "client")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_SEQ_GEN")
    @SequenceGenerator(name = "client_SEQ_GEN", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    @Column(length = 25)
    private String id;

    @Column(name = "client_id", length = 25, nullable = false)
    private String clientId;

    @Column(name = "client_id_issued_at", length = 25)
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret", nullable = false, length = 90)
    private String clientSecret;

    @Column(name = "client_secret_expires_at", length = 25)
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name", length = 100)
    private String clientName;

    @Column(name = "client_authentication_methods", length = 50)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 50)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 100)
    private String redirectUris;

    @Column(name = "scopes", length = 35)
    private String scopes;

    @Column(name = "client_settings", length = 150)
    private String clientSettings;

    @Column(name = "token_settings", length = 650)
    private String tokenSettings;

}