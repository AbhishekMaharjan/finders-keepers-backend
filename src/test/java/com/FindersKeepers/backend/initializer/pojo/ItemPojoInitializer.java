package com.FindersKeepers.backend.initializer.pojo;

import com.FindersKeepers.backend.enums.item.ItemStatus;
import com.FindersKeepers.backend.pojo.model.DescriptiveAttributesPojo;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import lombok.Data;

import java.util.List;

@Data
public class ItemPojoInitializer {

    ItemPojo itemPojo;

    public ItemPojoInitializer() {
        AddressPojoInitializer addressPojoInitializer = new AddressPojoInitializer();
        itemPojo = new ItemPojo(1L,
                "nearest landmark",
                "description",
                null,
                ItemStatus.FOUND,
                List.of(1L, 2L),
                addressPojoInitializer.getAddressPojo(),
                new DescriptiveAttributesPojo(),
                1L);
    }
}
