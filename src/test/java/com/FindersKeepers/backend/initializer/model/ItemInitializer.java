package com.FindersKeepers.backend.initializer.model;

import com.FindersKeepers.backend.enums.item.ItemStatus;
import com.FindersKeepers.backend.model.Item;
import com.FindersKeepers.backend.model.ItemCategory;
import lombok.Data;

import java.util.List;

@Data
public class ItemInitializer {

    Item foundItem;
    Item lostItem;

    public ItemInitializer() {
        ItemCategory parentCategory = new ItemCategory(1L, 0, "Electronic devices", null, false);
        List<ItemCategory> itemCategories = List.of(parentCategory, new ItemCategory(1L, 1, "Phone", parentCategory, false));
        AddressInitializer addressInitializer = new AddressInitializer();

        foundItem = Item.builder()
                .itemStatus(ItemStatus.FOUND.name())
                .description("item description")
                .isDeleted(false)
                .location(addressInitializer.getAddress())
                .itemCategoryList(itemCategories).build();

        lostItem = Item.builder()
                .itemStatus(ItemStatus.LOST.name())
                .description("item description")
                .isDeleted(false)
                .location(addressInitializer.getAddress())
                .itemCategoryList(itemCategories).build();
    }
}
