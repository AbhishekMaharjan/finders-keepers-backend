package com.FindersKeepers.backend.pojo.model;

import com.FindersKeepers.backend.enums.item.ItemCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DescriptiveAttributesPojo {
    private String brandName;
    private String color;
    private String uniqueIdentifier;
    private ItemCondition itemCondition;
}
