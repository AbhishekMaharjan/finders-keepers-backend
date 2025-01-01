package com.FindersKeepers.backend.service.impl;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.ItemCategory;
import com.FindersKeepers.backend.pojo.model.ItemCategoryPojo;
import com.FindersKeepers.backend.repository.ItemCategoryRepository;
import com.FindersKeepers.backend.service.ItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ITEM_CATEGORY;

@Service
@RequiredArgsConstructor
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository itemCategoryRepository;
    private final CustomMessageSource customMessageSource;


    @Override
    public ItemCategory findById(Long id) {
        return itemCategoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(ITEM_CATEGORY)))
        );
    }

    @Override
    public Page<ItemCategory> findAllParentItemCategory(String searchKey, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return itemCategoryRepository.findAllByNameContainingIgnoreCaseAndParentCategoryIsNull(pageable, searchKey);
    }

    @Override
    public List<ItemCategory> findAllByParent(Long parentCategoryId) {
        return itemCategoryRepository.findAllByParentCategoryId(parentCategoryId);
    }

    @Override
    public ItemCategory save(ItemCategoryPojo itemCategoryPojo) {
        ItemCategory itemCategory;
        if(Objects.nonNull(itemCategoryPojo.getParentId())) {
            itemCategory = findById(itemCategoryPojo.getParentId());
            itemCategory.setLevel(1);
            itemCategory.setParentCategory(itemCategory);
            itemCategory.setName(itemCategoryPojo.getName());
        }
        else {
            itemCategory = new ItemCategory();
            itemCategory.setLevel(0);
            itemCategory.setName(itemCategoryPojo.getName());
        }
        return itemCategoryRepository.save(itemCategory);
    }
}
