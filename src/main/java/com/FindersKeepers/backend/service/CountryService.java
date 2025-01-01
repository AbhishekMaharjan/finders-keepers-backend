package com.FindersKeepers.backend.service;


import com.FindersKeepers.backend.model.Country;
import com.FindersKeepers.backend.projections.GetAllCountryProjection;

import java.util.List;

public interface CountryService {
    List<GetAllCountryProjection> getAllCountry(String searchKey);

    Country findById(Integer id);

}
