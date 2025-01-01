package com.FindersKeepers.backend.initializer.pojo;

import com.FindersKeepers.backend.pojo.model.AddressPojo;
import lombok.Data;

@Data
public class AddressPojoInitializer {

    AddressPojo addressPojo;

    public AddressPojoInitializer() {
        addressPojo = new AddressPojo("street", "44600", "ktm", "bagmati", 157, 22.25, 85.25);
    }
}
