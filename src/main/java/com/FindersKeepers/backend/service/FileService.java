package com.FindersKeepers.backend.service;

import com.FindersKeepers.backend.enums.FileType;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    File findById(Long id) throws NotFoundException;

    File save(MultipartFile multipartFile, FileType fileType) throws IOException;
}
