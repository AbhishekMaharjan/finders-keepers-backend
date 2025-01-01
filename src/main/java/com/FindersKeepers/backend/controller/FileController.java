package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.enums.FileType;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.service.FileService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.FILES;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.SUCCESS_SAVE;

@RestController
@RequestMapping(value = "/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileService fileService;

    @Operation(
            summary = "API to upload items photo.",
            description = "Save photo/file to the system to retrieve file id and save while reporting lost items."
    )
    @PostMapping(path = "/item-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalApiResponse> savePublicFile(@Valid @ModelAttribute MultipartFile multipartFile,
                                                            @RequestParam FileType fileType) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(successResponse(customMessageSource.get(SUCCESS_SAVE,
                        customMessageSource.get(FILES)), fileService.save(multipartFile, fileType)));
    }
}
