package com.FindersKeepers.backend.service;


import com.FindersKeepers.backend.model.Address;
import com.FindersKeepers.backend.pojo.model.AddressPojo;

public interface AddressService {
    Address save(AddressPojo addressPojo, Integer addressId);

    Address findById(Integer id);
}
