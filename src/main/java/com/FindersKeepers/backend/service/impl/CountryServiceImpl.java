package com.FindersKeepers.backend.service.impl;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.Country;
import com.FindersKeepers.backend.projections.GetAllCountryProjection;
import com.FindersKeepers.backend.repository.CountryRepository;
import com.FindersKeepers.backend.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.COUNTRY;


@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CustomMessageSource customMessageSource;

    @Override
    public List<GetAllCountryProjection> getAllCountry(String name) {
        return countryRepository.getAll(name);
    }

    @Override
    public Country findById(Integer id) {
        return countryRepository.findById(id).orElseThrow(() -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(COUNTRY))));
    }

}
