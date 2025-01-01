package com.FindersKeepers.backend.initializer.model;

import com.FindersKeepers.backend.model.Address;
import lombok.Data;

@Data
public class AddressInitializer {

    public Address address;

    public AddressInitializer() {
        CountryInitializer countryInitializer = new CountryInitializer();
        address = new Address(1, "street", "44600", "ktm", "bagmati", countryInitializer.getCountry(), false, 22.25, 85.25);
    }
}
