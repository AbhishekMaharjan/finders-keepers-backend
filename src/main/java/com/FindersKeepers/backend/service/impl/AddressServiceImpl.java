package com.FindersKeepers.backend.service.impl;


import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.Address;
import com.FindersKeepers.backend.pojo.model.AddressPojo;
import com.FindersKeepers.backend.repository.AddressRepository;
import com.FindersKeepers.backend.service.AddressService;
import com.FindersKeepers.backend.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ADDRESS;


@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryService countryService;
    private final CustomMessageSource customMessageSource;

    @Override
    public Address findById(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(ADDRESS))));
    }

    @Override
    @Transactional
    public Address save(AddressPojo addressPojo, Integer addressId) {
        Address address;
        if (Objects.nonNull(addressId))
            address = findById(addressId);
        else
            address = new Address();
        address.setStreet(addressPojo.getStreet() == null ? null : addressPojo.getStreet().trim());
        address.setZipCode(addressPojo.getZipCode() == null ? null : addressPojo.getZipCode().trim());
        address.setCity(addressPojo.getCity() == null ? null : addressPojo.getCity().trim());
        address.setProvince(addressPojo.getProvince() == null ? null : addressPojo.getProvince().trim());
        address.setCountry(countryService.findById(addressPojo.getCountryId()));
        address.setLatitude(addressPojo.getLatitude() == null ? null : addressPojo.getLatitude());
        address.setLongitude(addressPojo.getLongitude() == null ? null : addressPojo.getLongitude());
        return addressRepository.save(address);
    }

}
