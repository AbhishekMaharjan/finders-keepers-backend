package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.service.CountryService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.COUNTRY;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.FETCHED_LIST;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/country")
public class CountryController extends BaseController {

    private final CountryService countryService;

    @Operation(
            summary = "Public API to Get all countries list drop down."
    )
    @GetMapping
    ResponseEntity<GlobalApiResponse> countyDropDown(@RequestParam(defaultValue = "", value = "searchKey") String searchKey) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(COUNTRY)), countryService.getAllCountry(searchKey)));
    }
}
