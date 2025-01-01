package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.enums.item.ItemStatus;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.pojo.model.ItemPojo;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.service.ItemService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.FOUND_ITEM;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.LOST_ITEM;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.FETCHED_LIST;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.SUCCESS_SAVE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/item")
public class ItemController extends BaseController {
    private final ItemService itemService;

    @Operation(
            summary = "API to save both lost and found items.",
            description = "Saves the provided item details to the system. Returns a success response if saved successfully. " +
                    "PS: You can get itemPhotoId by saving file from file controller."
    )
    @PostMapping(value = "save")
    public ResponseEntity<GlobalApiResponse> save(@Valid @RequestBody ItemPojo itemPojo) throws NotFoundException {
        String item = itemPojo.getItemStatus().name().equals("LOST") ? LOST_ITEM : FOUND_ITEM;
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(successResponse(customMessageSource.get(SUCCESS_SAVE,
                        customMessageSource.get(item)), itemService.save(itemPojo)));
    }


    @Operation(
            summary = "Get all lost/found items.",
            description = "Fetches a list of items with filters by status and item category."
    )
    @GetMapping
    ResponseEntity<GlobalApiResponse> getAllItems(@RequestParam(defaultValue = "LOST", required = false) ItemStatus filterItemByStatus,
                                                  @RequestParam(defaultValue = "", required = false) Long itemCategoryId,
                                                  @RequestParam(defaultValue = "0", required = false) int page,
                                                  @RequestParam(defaultValue = "10", required = false) int size) {
        String item = filterItemByStatus.name().equals("LOST") ? LOST_ITEM : FOUND_ITEM;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(item)), itemService.findAllItems(filterItemByStatus.name(), page, size, itemCategoryId)));
    }


    @Operation(
            summary = "Get best match found items for a lost item.",
            description = "Fetches a list of items that are best match for the lost items reported based on category and nearest location."
    )
    @GetMapping(value = "/best-match/{lostItemId}")
    ResponseEntity<GlobalApiResponse> getBestMatchItems(@PathVariable Long lostItemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(FOUND_ITEM)), itemService.findBestMatchItemsByItemId(lostItemId)));
    }

}
