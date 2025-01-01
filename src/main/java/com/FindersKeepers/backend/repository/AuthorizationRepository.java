package com.FindersKeepers.backend.repository;


import com.FindersKeepers.backend.model.auth.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AuthorizationRepository extends JpaRepository<Authorization, String> {
    Optional<Authorization> findByAccessTokenValue(String accessToken);

    Optional<Authorization> findByRefreshTokenValue(String refreshToken);

    Optional<Authorization> findByPrincipalNameAndRegisteredClientId(String username, String registerClientId);

    @Query("select a from Authorization a where a.accessTokenValue = :token" +
            " or a.refreshTokenValue = :token")
    Optional<Authorization> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(@Param("token") String token);

}