package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Item findById(Long id);

    Item save(ItemPojo itemPojo);

    Page<Item> findAllItems(String itemStatus, int page, int size, Long itemCategoryId);

    List<Item> findBestMatchItemsByItemId(Long lostItemId);

    void changeItemStatus(Long itemId);
}
