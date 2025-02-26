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

import com.lnf.company.model.Company;
import com.lnf.company.repository.CompanyPolicyRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.CompanyPolicyDto;
import com.lnf.dto.file.FileDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyPolicyService {

    public static final String POLICIES = "policies";
    public static final String S_S_S = "%s/%s/%s/";

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    private final CompanyRepository companyRepository;
    private final CompanyPolicyRepository repository;
    private final FileService fileService;
    private final FileFolderService fileFolderService;

    public List<CompanyPolicyDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        String filePath = S_S_S.formatted(folderName, companyId, POLICIES);
        List<FileDto> files = fileFolderService.findFiles(filePath);
        List<CompanyPolicyDto> companyPolicyDtos = new ArrayList<>();
        files.forEach(file -> {
            String fileName = StringUtils.substringAfterLast(file.getFileName(), "/");
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/lnf/company/%s/policies/%s".formatted(companyId, fileName))
                    .toUriString();
            setCompanyPolicyDto(companyPolicyDtos, file, fileName, downloadURL);
        });
        return companyPolicyDtos;
    }

    private void setCompanyPolicyDto(List<CompanyPolicyDto> companyPolicyDtos, FileDto file, String fileName,
                                     String downloadURL) {
        CompanyPolicyDto companyPolicyDto = new CompanyPolicyDto();
        companyPolicyDto.setName(fileName);
        companyPolicyDto.setUrl(downloadURL);
        companyPolicyDto.setSize(file.getFileSize());
        companyPolicyDtos.add(companyPolicyDto);
    }

    public ResponseEntity<byte[]> findById(UUID companyId, String fileName) {
        try {
            searchForCompany(companyId);
            String filePath = "%s/%s/%s/%s".formatted(folderName, companyId, POLICIES, fileName);
            return fileService.findFileContent(filePath);
        } catch (RuntimeException e) {
            String errorMessage = "file not found for Company[%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile[] policies) {
        Company company = searchForCompany(companyId);
        for (MultipartFile policy : policies) {
            try {
                LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                        "Failed to create Policy for company [%s] with null payload".formatted(companyId));
                searchForCompany(companyId);
                String folder = S_S_S.formatted(folderName, companyId, POLICIES);
                String filePath = uploadFile(folder, policy);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } catch (RuntimeException e) {
                String errorMessage = "Failed to create policy[%s] for company [%s]".formatted(
                        companyId, policy.getOriginalFilename());
                throw new LnFException(errorMessage, e);
            }
        }
    }

    public void update(UUID companyId, MultipartFile policy) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                "Failed to update policy for company [%s] with null payload".formatted(companyId));
        try {
            searchForCompany(companyId);
            String folder = S_S_S.formatted(folderName, companyId, POLICIES);
            String filePath = uploadFile(folder, policy);
            log.debug("File uploaded successfully to S3 bucket: " + filePath);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to update policy for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage, e);
        }
    }


    public void deleteById(UUID companyId, String fileName) {
        searchForCompany(companyId);
        String s3ObjectKey = "%s/%s/%s/%s".formatted(folderName, companyId, POLICIES, fileName);
        List<String> filePaths = Collections.singletonList(s3ObjectKey);
        fileService.delete(filePaths);
        log.debug("S3 object deleted for company");
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(
                        "Company with id [%s] does not exist".formatted(companyId)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
