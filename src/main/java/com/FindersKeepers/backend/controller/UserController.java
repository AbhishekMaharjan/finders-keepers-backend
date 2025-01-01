package com.FindersKeepers.backend.controller;

import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.pojo.model.UserPojo;
import com.FindersKeepers.backend.pojo.util.GlobalApiResponse;
import com.FindersKeepers.backend.records.UsersUpdateRecord;
import com.FindersKeepers.backend.service.UserService;
import com.FindersKeepers.backend.util.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.FindersKeepers.backend.constant.message.FieldConstantValue.USER;
import static com.FindersKeepers.backend.constant.message.SuccessResponseConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController extends BaseController {

    private final UserService userService;


    @Operation(
            summary = "public API to save a new user",
            description = "Saves the provided user details to the system."
    )
    @PostMapping(value = "save")
    public ResponseEntity<GlobalApiResponse> save(@Valid @RequestBody UserPojo userPojo) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(successResponse(customMessageSource.get(SUCCESS_SAVE,
                        customMessageSource.get(USER)), userService.save(userPojo)));
    }

    @PutMapping(value = "{userId}")
    public ResponseEntity<GlobalApiResponse> update(@Valid @RequestBody UsersUpdateRecord usersUpdateRecord,
                                                    @PathVariable Long userId) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(successResponse(customMessageSource.get(SUCCESS_UPDATE,
                        customMessageSource.get(USER)), userService.update(usersUpdateRecord, userId)));
    }

    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users in the system."
    )
    @GetMapping
    ResponseEntity<GlobalApiResponse> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(successResponse(customMessageSource.get(FETCHED_LIST,
                        customMessageSource.get(USER)), userService.getAllUsers()));
    }
}
