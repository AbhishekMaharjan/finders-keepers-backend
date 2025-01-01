package com.FindersKeepers.backend.projections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetAllCountryProjection {
    Integer id;
    String name;
    String code;
}
