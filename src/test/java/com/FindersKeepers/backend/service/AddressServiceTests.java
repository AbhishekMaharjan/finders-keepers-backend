package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.initializer.model.AddressInitializer;
import com.FindersKeepers.backend.initializer.pojo.AddressPojoInitializer;
import com.FindersKeepers.backend.model.Address;
import com.FindersKeepers.backend.pojo.model.AddressPojo;
import com.FindersKeepers.backend.repository.AddressRepository;
import com.FindersKeepers.backend.service.impl.AddressServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "spring.liquibase.enabled=false")
public class AddressServiceTests {

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Mock
    private CustomMessageSource customMessageSource;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private CountryService countryService;

    private AddressInitializer addressInitializer;

    private AddressPojoInitializer addressPojoInitializer;

    @BeforeEach
    public void init() {

        addressInitializer = new AddressInitializer();
        addressPojoInitializer = new AddressPojoInitializer();
    }

    @Test
    void AddressService_CheckIt_ReturnsAddress() {

        Address mockAddress = addressInitializer.getAddress();
        when(addressRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(mockAddress));

        Address address = addressService.findById(1);

        Assertions.assertThat(address).isNotNull();
    }


    @Test
    void AddressService_CheckItWhenWrongIdProvided_ReturnsNotFoundError() {
        when(customMessageSource.get("address"))
                .thenReturn("Address");

        when(customMessageSource.get("error.id.not.found", customMessageSource.get("address")))
                .thenThrow(new NotFoundException("Address id not found"));

        when(addressRepository.findById(100)).thenReturn(Optional.empty());
        try {
            addressService.findById(100);
        } catch (Exception ex) {
            assertAll(
                    () -> Assertions.assertThat(ex.getMessage()).isEqualTo("Address id not found"),
                    () -> Assertions.assertThat(ex.getClass().getName()).isEqualTo(NotFoundException.class.getName())
            );
        }
    }

    @Test
    void AddressService_SaveAddress_ReturnsAddress() {
        Address mockAddress = new Address();

        AddressPojo addressPojo = addressPojoInitializer.getAddressPojo() ;

        when(addressRepository.save(Mockito.any(Address.class))).thenReturn(mockAddress);

        Address saved = addressService.save(addressPojo, null);

        Assertions.assertThat(saved).isNotNull();
        Assertions.assertThat(saved).isEqualTo(mockAddress);
    }
}
