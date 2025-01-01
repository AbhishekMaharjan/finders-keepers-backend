package com.FindersKeepers.backend.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCategoryPojo {
    private String name;
    private Long parentId;
}
