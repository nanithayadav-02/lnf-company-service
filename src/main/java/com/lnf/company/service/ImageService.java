/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.service;

import com.lnf.company.converter.ImageConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.Image;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.company.repository.ImageRepository;
import com.lnf.dto.company.ImageDto;
import com.lnf.dto.file.FileDto;
import com.lnf.service.file.FileFolderService;
import com.lnf.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    public static final String S_S_S = "%s/%s/%s/";
    public static final String IMAGE = "image";
    private final CompanyRepository companyRepository;
    private final ImageRepository repository;
    private final FileService fileService;
    private final FileFolderService fileFolderService;

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    public ImageDto findByCompanyId(UUID companyId) {
        if (awsS3BucketEnabled) {
            String filePath = S_S_S.formatted(folderName, companyId, IMAGE);
            List<FileDto> filePaths = fileFolderService.findFiles(filePath);
            if (filePaths == null || filePaths.isEmpty()) {
                log.debug("Image not found for company {}", companyId);
                return null;
            }
            String fileName = StringUtils.substringAfterLast(filePaths.get(0).getFileName(), "/");
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/lnf/company/%s/image/%s".formatted(companyId, fileName))
                    .toUriString();

            ImageDto imageDto = new ImageDto();
            imageDto.setName(fileName);
            imageDto.setSize(filePaths.get(0).getFileSize());
            imageDto.setUrl(url);

            return imageDto;
        } else {
            searchForCompany(companyId);
            Image entity = repository.findByCompanyId(companyId);
            if (entity == null) {
                throw new LnFEntityNotFoundException("Image for company [%s] does not exist".formatted(companyId));
            }

            ImageDto imageDto = ImageConverter.toTransportModel(entity);
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/lnf/company/%s/image/".formatted(companyId))
                    .path(imageDto.getId().toString())
                    .toUriString();
            imageDto.setUrl(downloadURL);

            return imageDto;
        }
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID imageId, String fileName) {
        try {
            if (awsS3BucketEnabled) {
                String filePath = "%s/%s/%s/%s".formatted(folderName, companyId, IMAGE, fileName);
                return fileService.findFileContent(filePath);
            } else {
                searchForCompany(companyId);
                Image file = searchForImage(imageId);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentType(MediaType.valueOf(file.getContentType()))
                        .body(file.getContent());
            }
        } catch (RuntimeException e) {
            String errorMessage = "file not found for Company[%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile file) {
        try {
            LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                    "Failed to create Image for company [%s] with null payload".formatted(companyId));
            if (awsS3BucketEnabled) {
                String folder = S_S_S.formatted(folderName, companyId, IMAGE);
                String filePath = uploadFile(folder, file);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } else {
                Company company = searchForCompany(companyId);
                Image entity = ImageConverter.toEntityModel(file, false);
                entity.setCompany(company);
                save(entity);
                log.debug("Image {} for Company {} successfully created",
                        file.getOriginalFilename(), companyId);
            }
        } catch (RuntimeException | IOException e) {

            String errorMessage = "Failed to create image[%s] for company [%s]".formatted(companyId,
                    file.getOriginalFilename());
            throw new LnFException(errorMessage, e);
        }
    }

    public void update(UUID companyId, UUID fileId, MultipartFile file) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                "Failed to update file for company [%s] with null payload".formatted(companyId));
        try {
            if (awsS3BucketEnabled) {
                String folder = S_S_S.formatted(folderName, companyId, IMAGE);
                String filePath = uploadFile(folder, file);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } else {
                searchForCompany(companyId);
                Image entity = searchForImage(fileId);
                Image updatedEntity = ImageConverter.toEntityModel(file, entity, false);
                save(updatedEntity);
                log.debug("Image {} for Company {} successfully updated", fileId, companyId);
            }
        } catch (RuntimeException | IOException e) {
            String errorMessage = "Failed to update file[%s] for company [%s]".formatted(fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        Image entity = repository.findByCompanyId(companyId);
        try {
            repository.delete(entity);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete image for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }

    }

    public void deleteById(UUID companyId, UUID fileId, String fileName) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = "%s/%s/%s/%s".formatted(folderName, companyId, IMAGE, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            log.debug("S3 object deleted for company");
        } else {
            searchForCompany(companyId);
            Image entity = searchForImage(fileId);
            try {
                repository.delete(entity);
                log.debug("Image {} for company {} successfully deleted", fileId, companyId);
            } catch (RuntimeException e) {
                String errorMessage = "Failed to delete Image[[%s] for company [%s]".formatted(fileId, companyId);
                throw new LnFException(errorMessage);
            }
        }
    }

    private void save(Image entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Image for company [%s]",
                    entity.getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(
                        companyId)));
    }

    private Image searchForImage(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException("Image with id [%s] does not exist".formatted(
                        fileId)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
