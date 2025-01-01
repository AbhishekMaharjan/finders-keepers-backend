package com.FindersKeepers.backend.initializer.model;

import com.FindersKeepers.backend.model.Country;
import lombok.Data;

@Data
public class CountryInitializer {

    Country country;

    public CountryInitializer() {
        country = new Country(1, "Nepal", "NEP");
    }
}
