package com.FindersKeepers.backend.service.impl;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.DescriptiveAttributes;
import com.FindersKeepers.backend.pojo.model.DescriptiveAttributesPojo;
import com.FindersKeepers.backend.repository.DescriptiveAttributesRepository;
import com.FindersKeepers.backend.service.DescriptiveAttributesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.DESCRIPTIVE_ATTRIBUTES;

@RequiredArgsConstructor
@Service
public class DescriptiveAttributesServiceImpl implements DescriptiveAttributesService {

    private final DescriptiveAttributesRepository descriptiveAttributesRepository;
    private final CustomMessageSource customMessageSource;

    @Override
    public DescriptiveAttributes findById(Long id) {
        return descriptiveAttributesRepository.findById(id).orElseThrow(() -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(DESCRIPTIVE_ATTRIBUTES))));
    }

    @Override
    @Transactional
    public DescriptiveAttributes save(DescriptiveAttributesPojo descriptiveAttributesPojo) {
        DescriptiveAttributes descriptiveAttributes = new DescriptiveAttributes();
        descriptiveAttributes.setBrand(descriptiveAttributesPojo.getBrandName());
        descriptiveAttributes.setColor(descriptiveAttributesPojo.getColor());
        descriptiveAttributes.setItemCondition(descriptiveAttributesPojo.getItemCondition().name());
        return descriptiveAttributesRepository.save(descriptiveAttributes);
    }
}
