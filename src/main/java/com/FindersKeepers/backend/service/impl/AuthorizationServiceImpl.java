package com.FindersKeepers.backend.service.impl;



import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.SessionException;
import com.FindersKeepers.backend.model.Users;
import com.FindersKeepers.backend.repository.AuthorizationRepository;
import com.FindersKeepers.backend.repository.UserRepository;
import com.FindersKeepers.backend.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_FIND_USER_PRINCIPLE_SESSION;


@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    private final AuthorizationRepository authorizationRepository;
    private final UserRepository userRepository;
    private final CustomMessageSource customMessageSource;

    @Override
    public Users findUsersByPrinciple(String principle) {
        return userRepository.findByEmail(principle).orElseThrow(() -> new SessionException(customMessageSource.get(ERROR_FIND_USER_PRINCIPLE_SESSION)));
    }
}