package com.FindersKeepers.backend.util;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.model.Users;
import com.FindersKeepers.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_VALIDATION;
import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.NOT_AUTHENTICATED;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.USER;


@Repository
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final CustomMessageSource customMessageSource;

    public Users getAuthenticatedUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Object object = jwt.getClaim("user");
        if (object instanceof Map<?, ?> map) {
            Optional<Users> optionalAuthUser = userRepository.findByEmail((map.get("username")).toString());
            if (optionalAuthUser.isEmpty()) {
                throw new Exception(customMessageSource.get(NOT_AUTHENTICATED, customMessageSource.get(USER)));
            }
            return optionalAuthUser.get();
        }
        throw new Exception(customMessageSource.get(ERROR_VALIDATION, customMessageSource.get("claims")));
    }

}