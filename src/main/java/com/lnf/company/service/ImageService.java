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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    public ImageDto findByCompanyId(UUID companyId) {
        String filePath = S_S_S.formatted(folderName, companyId, IMAGE);
        List<FileDto> filePaths = fileFolderService.findFiles(filePath);
        if (filePaths == null || filePaths.isEmpty()) {
            log.debug("Image not found for company {}", companyId);
            return null;
        }
        String fileName = StringUtils.substringAfterLast(filePaths.getFirst().getFileName(), "/");
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/lnf/company/%s/image/%s".formatted(companyId, fileName))
                .toUriString();

        ImageDto imageDto = new ImageDto();
        imageDto.setName(fileName);
        imageDto.setSize(filePaths.getFirst().getFileSize());
        imageDto.setUrl(url);

        return imageDto;
    }

    public ResponseEntity<byte[]> findById(UUID companyId, String fileName) {
        try {
            searchForCompany(companyId);
            String filePath = "%s/%s/%s/%s".formatted(folderName, companyId, IMAGE, fileName);
            return fileService.findFileContent(filePath);
        } catch (RuntimeException e) {
            String errorMessage = "file not found for Company[%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile file) {
        try {
            LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                    "Failed to create Image for company [%s] with null payload".formatted(companyId));
            searchForCompany(companyId);
            String folder = S_S_S.formatted(folderName, companyId, IMAGE);
            String filePath = uploadFile(folder, file);
            log.debug("File uploaded successfully to S3 bucket: " + filePath);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to create image[%s] for company [%s]".formatted(companyId,
                    file.getOriginalFilename());
            throw new LnFException(errorMessage, e);
        }
    }

    public void update(UUID companyId, MultipartFile file) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                "Failed to update file for company [%s] with null payload".formatted(companyId));
        try {
            searchForCompany(companyId);
            String folder = S_S_S.formatted(folderName, companyId, IMAGE);
            String filePath = uploadFile(folder, file);
            log.debug("File uploaded successfully to S3 bucket: " + filePath);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to update file for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteById(UUID companyId, String fileName) {
        searchForCompany(companyId);
        String s3ObjectKey = "%s/%s/%s/%s".formatted(folderName, companyId, IMAGE, fileName);
        List<String> filePaths = Collections.singletonList(s3ObjectKey);
        fileService.delete(filePaths);
        log.debug("S3 object deleted for company");
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

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
