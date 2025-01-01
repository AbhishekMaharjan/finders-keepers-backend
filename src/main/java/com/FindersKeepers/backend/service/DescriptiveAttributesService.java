package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.model.DescriptiveAttributes;
import com.FindersKeepers.backend.pojo.model.DescriptiveAttributesPojo;

public interface DescriptiveAttributesService {
    DescriptiveAttributes findById(Long id);

    DescriptiveAttributes save(DescriptiveAttributesPojo descriptiveAttributesPojo);
}
