package com.FindersKeepers.backend.service.impl;

import com.FindersKeepers.backend.config.resources.CustomMessageSource;
import com.FindersKeepers.backend.enums.FileType;
import com.FindersKeepers.backend.exception.NotFoundException;
import com.FindersKeepers.backend.model.File;
import com.FindersKeepers.backend.repository.FileRepository;
import com.FindersKeepers.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;

import static com.FindersKeepers.backend.constant.message.ErrorConstantValue.ERROR_ID_NOT_FOUND;
import static com.FindersKeepers.backend.constant.message.FieldConstantValue.FILES;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileRepository fileRepository;
    private final CustomMessageSource customMessageSource;
    @Value("${file.upload-directory}")
    private String uploadDir;

    @Override
    public File findById(Long id) throws NotFoundException {
        Optional<File> image = fileRepository.findById(id);
        if (image.isEmpty()) {
            throw new NotFoundException(customMessageSource.get(ERROR_ID_NOT_FOUND, customMessageSource.get(FILES)));
        }
        return image.get();
    }

    @Override
    @Transactional
    public File save(MultipartFile multipartFile, FileType fileType) throws IOException {

        Path directory = Paths.get(uploadDir);
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            logger.error("Failed to create directories: {}", directory, e);
            throw new IOException("Could not create upload directory", e);
        }

        Path filePath = directory.resolve(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        System.out.println(filePath);
        try {
            Files.write(filePath, multipartFile.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            logger.error("Failed to save file: {}", filePath, e);
            throw new IOException("Could not save file", e);
        }
        File file = new File();
        file.setFileType(fileType.name());
        file.setPath(filePath.toString().replace("\\", "/"));
        file.setStatus(true);
        return fileRepository.save(file);
    }

}
