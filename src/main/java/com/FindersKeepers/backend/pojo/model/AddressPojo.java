package com.FindersKeepers.backend.pojo.model;


import com.FindersKeepers.backend.constant.message.FieldErrorConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressPojo {
    private String street;
    private String zipCode;
    private String city;
    private String province;
    private Integer countryId;

    @NotNull(message = FieldErrorConstants.NOT_NULL)
    private Double latitude;

    @NotNull(message = FieldErrorConstants.NOT_NULL)
    private Double longitude;

}
