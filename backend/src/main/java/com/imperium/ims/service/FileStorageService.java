package com.imperium.ims.service;

import com.imperium.ims.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (originalFilename.contains("..")) {
                throw new BusinessException("Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String targetFilename = UUID.randomUUID().toString() + extension;
            Path targetLocation = this.fileStorageLocation.resolve(targetFilename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + targetFilename; // Standard URL path for frontend to consume
        } catch (IOException ex) {
            throw new BusinessException("Could not store file " + originalFilename + ". Please try again!");
        }
    }
}
