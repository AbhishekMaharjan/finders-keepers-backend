package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.pojo.model.ItemCategoryPojo;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.service.ItemCategoryService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.ITEM_CATEGORY;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.FETCHED_LIST;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.SUCCESS_SAVE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/item-category")
public class ItemCategoryController extends BaseController {

    private final ItemCategoryService itemCategoryService;

    @Operation(
            summary = "Public API to Get all parent item categories.",
            description = "Fetches a list of all parent item categories with optional search and pagination."
    )
    @GetMapping
    ResponseEntity<GlobalApiResponse> findAllParent(@RequestParam(defaultValue = "", required = false) String searchKey,
                                                    @RequestParam(defaultValue = "0", required = false) int page,
                                                    @RequestParam(defaultValue = "100", required = false) int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(ITEM_CATEGORY)), itemCategoryService.findAllParentItemCategory(searchKey, page, size)));
    }


    @Operation(
            summary = "Public API to Get child categories of a parent category.",
            description = "Fetches a list of child categories for a given parent category."
    )
    @GetMapping("{parentCategoryId}")
    ResponseEntity<GlobalApiResponse> findChildOfParentCategory(@PathVariable(value = "parentCategoryId") Long parentCategoryId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(ITEM_CATEGORY)), itemCategoryService.findAllByParent(parentCategoryId)));
    }

    @Operation(
            summary = "Save a custom new item category and its sub-categories.",
            description = "Saves a new item category to the system and returns the saved item category."
    )
    @PostMapping(value = "save")
    public ResponseEntity<GlobalApiResponse> save(@Valid @RequestBody ItemCategoryPojo itemCategoryPojo) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(successResponse(customMessageSource.get(SUCCESS_SAVE,
                        customMessageSource.get(ITEM_CATEGORY)), itemCategoryService.save(itemCategoryPojo)));
    }
}
