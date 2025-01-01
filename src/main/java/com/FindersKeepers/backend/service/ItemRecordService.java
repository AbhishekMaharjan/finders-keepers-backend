package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.model.ItemRecord;
import com.FindersKeepers.backend.pojo.model.ItemRecordPojo;

import java.io.IOException;
import java.util.List;

public interface ItemRecordService {
    ItemRecord findById(Long id);

    ItemRecord save(ItemRecordPojo itemRecordPojo) throws IOException;

    List<ItemRecord> getClaimRecords(Long userId);

    Boolean updateStatus(Long itemId);
}
