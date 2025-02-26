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

import com.lnf.company.converter.CompanyFileConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyFile;
import com.lnf.company.repository.CompanyFileRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.CompanyFileDto;
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

import java.util.*;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyFileService {

    public static final String S_S_S_S = "%s/%s/%s/%s";
    @Value("${aws.s3.bucket.folderName}")
    private String folderName;
    private static final String FAILED_TO_CREATE_COMPANY_FILE_NULL_PAYLOAD = "Failed to create companyFile for " +
            "company [%s] with null payload";
    public static final String FILES = "files";
    public static final String S_S_S = "%s/%s/%s/";
    private final CompanyFileRepository repository;
    private final CompanyRepository companyRepository;
    private final FileService fileService;
    private final FileFolderService fileFolderService;

    public List<CompanyFileDto> findByCompanyId(UUID companyId) {
        String filePath = S_S_S.formatted(folderName, companyId, FILES);
        List<FileDto> files = fileFolderService.findFiles(filePath);
        List<CompanyFileDto> companyFileDtos = new ArrayList<>();
        files.forEach(file -> {
            String fileName = StringUtils.substringAfterLast(file.getFileName(), "/");
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/lnf/company/%s/files/%s".formatted(companyId, fileName))
                    .toUriString();

            setCompanyFile(companyFileDtos, file, fileName, downloadURL);
        });
        return companyFileDtos;
    }

    private void setCompanyFile(List<CompanyFileDto> companyFileDtos, FileDto file, String fileName, String downloadURL) {
        CompanyFileDto companyFileDto = new CompanyFileDto();
        CompanyFile entity = searchForFileName(fileName);
        companyFileDto.setId(entity.getId());
        companyFileDto.setFileName(fileName);
        companyFileDto.setUrl(downloadURL);
        companyFileDto.setDescription(entity.getDescription());
        companyFileDto.setSize(file.getFileSize());
        companyFileDto.setLastModified(file.getLastModified());
        companyFileDtos.add(companyFileDto);
    }

    public ResponseEntity<byte[]> findById(UUID companyId, String fileName) {
        try {
            searchForFileName(fileName);
            String filePath = S_S_S_S.formatted(folderName, companyId, FILES, fileName);
            return fileService.findFileContent(filePath);
        } catch (RuntimeException e) {
            String errorMessage = "file not found for Company[%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile[] files, List<CompanyFileDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, FAILED_TO_CREATE_COMPANY_FILE_NULL_PAYLOAD.formatted(companyId));
        Company company = searchForCompany(companyId);
        List<CompanyFile> entities = new ArrayList<>();

        IntStream.range(0, files.length).forEach(i -> {
            MultipartFile file = files[i];
            CompanyFileDto companyFileDto = resource.get(i);
            try {
                CompanyFile entity = CompanyFileConverter.toEntityModel(companyFileDto, new CompanyFile());
                entity.setCompany(company);
                entities.add(entity);
                save(entities);

                String folder = S_S_S.formatted(folderName, companyId, FILES);
                String filePath = uploadFile(folder, file);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } catch (RuntimeException e) {
                String errorMessage = "Failed to create file[%s] for company [%s]".formatted(file.getName(), companyId);
                throw new LnFException(errorMessage, e);
            }
        });
    }

    public void update(UUID companyId, String fileName, MultipartFile file, CompanyFileDto resource) {
        com.lnf.exception.LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                "Failed to update file for company [%s] with null payload".formatted(companyId));
        try {
            CompanyFile entity = searchForFileName(fileName);
            CompanyFile updatedEntity = CompanyFileConverter.toEntityModel(resource, entity);
            save(updatedEntity);
            String s3ObjectKey = S_S_S_S.formatted(folderName, companyId, FILES, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            //Before Updating the file we are deleting from the s3 bucket
            String folder = S_S_S.formatted(folderName, companyId, FILES);
            String filePath = uploadFile(folder, file);
            log.debug("File uploaded successfully to S3 bucket: " + filePath);
            log.debug("fileName {} for Company {} successfully updated", fileName, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to update fileName[%s] for company [%s]".formatted(fileName, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByIdAndFileName(UUID companyId, String fileName) {
        searchForCompany(companyId);
        CompanyFile entity = searchForFileName(fileName);
        try {
            String s3ObjectKey = S_S_S_S.formatted(folderName, companyId, FILES, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            log.debug("S3 object deleted for company file");
            repository.delete(entity);
            log.debug("file {} for company {} successfully deleted", fileName, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete File[[%s] for company [%s]".formatted(fileName, companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyFile> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyFile for company [%s]", entities.getFirst().getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyFile entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyFile for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    private CompanyFile searchForFileName(String fileName) {
        return repository.findByFileName(fileName).
                orElseThrow(() -> new LnFEntityNotFoundException(
                        "Company file with fileName [%s] does not exist".formatted(fileName)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
