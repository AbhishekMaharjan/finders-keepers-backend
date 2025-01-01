package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.model.ItemCategory;
import com.FindersKeepers.backend.pojo.model.ItemCategoryPojo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemCategoryService {

    ItemCategory findById(Long id);

    Page<ItemCategory> findAllParentItemCategory(String searchKey, int page, int size);

    List<ItemCategory> findAllByParent(Long parentCategoryId);

    ItemCategory save(ItemCategoryPojo itemCategoryPojo);

}
