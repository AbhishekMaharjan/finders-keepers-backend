package com.FindersKeepers.backend.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRecordPojo {
    private String remarks;
    private Long claimUserId;
    private Long itemId;
    private MultipartFile proofImage;
}
