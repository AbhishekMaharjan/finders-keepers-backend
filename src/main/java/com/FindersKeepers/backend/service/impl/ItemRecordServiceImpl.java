package com.FindersKeepers.backend.service.impl;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.enums.FileType;
import com.FindersKeepers.backend.enums.item.ItemRecordStatus;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.ItemRecord;
import com.FindersKeepers.backend.pojo.model.ItemRecordPojo;
import com.FindersKeepers.backend.repository.ItemRecordRepository;
import com.FindersKeepers.backend.service.FileService;
import com.FindersKeepers.backend.service.ItemRecordService;
import com.FindersKeepers.backend.service.ItemService;
import com.FindersKeepers.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ITEM_RECORD;

@Service
@RequiredArgsConstructor
public class ItemRecordServiceImpl implements ItemRecordService {

    private final ItemRecordRepository itemRecordRepository;
    private final CustomMessageSource customMessageSource;
    private final UserService userService;
    private final ItemService itemService;
    private final FileService fileService;

    @Override
    public ItemRecord findById(Long id) {
        return itemRecordRepository.findById(id).orElseThrow(
                () -> new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(ITEM_RECORD)))
        );
    }

    @Override
    public List<ItemRecord> getClaimRecords(Long userId) {
        return itemRecordRepository.findAllByClaimUserId(userId);
    }

    @Override
    @Transactional
    public ItemRecord save(ItemRecordPojo itemRecordPojo) throws IOException {
        ItemRecord itemRecord = new ItemRecord();
        itemRecord.setRemarks(itemRecordPojo.getRemarks());
        itemRecord.setStatus(ItemRecordStatus.PENDING.name());
        itemRecord.setClaimUser(userService.findById(itemRecordPojo.getClaimUserId()));
        itemRecord.setItem(itemService.findById(itemRecordPojo.getItemId()));
        itemRecord.setProofImage(fileService.save(itemRecordPojo.getProofImage(), FileType.PROOF_FILES));
        return itemRecordRepository.save(itemRecord);
    }

    @Transactional
    @Override
    public Boolean updateStatus(Long itemId) {
        ItemRecord itemRecord = findById(itemId);
        itemRecord.setStatus(ItemRecordStatus.APPROVED.name());
        itemRecordRepository.save(itemRecord);
        itemService.changeItemStatus(itemId);
        return true;
    }
}
