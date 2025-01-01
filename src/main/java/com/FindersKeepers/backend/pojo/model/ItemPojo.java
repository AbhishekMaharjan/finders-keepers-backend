package com.FindersKeepers.backend.pojo.model;

import com.FindersKeepers.backend.enums.item.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPojo {
    private Long userId;
    private String nearestLandmark;
    private String description;
    private LocalDateTime reportedDate;
    private ItemStatus itemStatus;
    private List<Long> itemCategoryIds;
    private AddressPojo addressPojo;
    private DescriptiveAttributesPojo descriptiveAttributesPojo;
    private Long itemPhotoId;
}
