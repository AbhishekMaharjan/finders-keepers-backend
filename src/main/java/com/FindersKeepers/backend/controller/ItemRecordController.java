package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.pojo.model.ItemRecordPojo;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.service.ItemRecordService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ITEM_RECORD;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/item-record")
public class ItemRecordController extends BaseController {

    private final ItemRecordService itemRecordService;

    @Operation(
            summary = "API to save request claim to found items.",
            description = "Saves the claim item record to the system with status pending for approval."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalApiResponse> save(@Valid @ModelAttribute ItemRecordPojo itemRecordPojo) throws NotFoundException, IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(successResponse(customMessageSource.get(SUCCESS_SAVE,
                        customMessageSource.get(ITEM_RECORD)), itemRecordService.save(itemRecordPojo)));
    }

    @Operation(
            summary = "API to fetch item claim records of user."
    )
    @GetMapping(value = "{userId}")
    public ResponseEntity<GlobalApiResponse> findItemRecords(@PathVariable Long userId) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(ITEM_RECORD)), itemRecordService.getClaimRecords(userId)));
    }

    @Operation(
            summary = "API to approve item claim after verifying proof records."
    )
    @PatchMapping(value = "{itemId}")
    public ResponseEntity<GlobalApiResponse> change(@PathVariable Long itemId) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(successResponse(customMessageSource.get(ITEM_CLAIM_APPROVED),
                        itemRecordService.updateStatus(itemId)));
    }

}
