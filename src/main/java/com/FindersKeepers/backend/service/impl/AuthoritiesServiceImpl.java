package com.FindersKeepers.backend.service.impl;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.auth.Authorities;
import com.FindersKeepers.backend.repository.AuthoritiesRepository;
import com.FindersKeepers.backend.service.AuthoritiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.AUTHORITIES;


@Service
@RequiredArgsConstructor
public class AuthoritiesServiceImpl implements AuthoritiesService {
    private final AuthoritiesRepository authoritiesRepository;
    private final CustomMessageSource customMessageSource;


    @Override
    public Authorities findByName(String name) {
        return authoritiesRepository.findByAuthority(name).orElseThrow(() -> new NotFoundException(customMessageSource.get(ERROR_NOT_FOUND, customMessageSource.get(AUTHORITIES))));
    }
}
