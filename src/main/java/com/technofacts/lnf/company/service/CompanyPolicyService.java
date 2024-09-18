package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.CompanyPolicyConverter;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyPolicy;
import com.technofacts.lnf.company.repository.CompanyPolicyRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.CompanyPolicyDto;
import com.technofacts.lnf.exception.LnFBadRequestException;
import com.technofacts.lnf.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.exception.LnFException;
import com.technofacts.lnf.service.file.FileService;
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
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyPolicyService {

    public static final String POLICIES = "policies";
    public static final String S_S_S = "%s/%s/%s/";

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    private final CompanyRepository companyRepository;

    private final CompanyPolicyRepository repository;

    private final FileService fileService;

    public List<CompanyPolicyDto> findAll() {
        List<CompanyPolicy> entities = repository.findAll();
        return entities.stream().map(CompanyPolicyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<CompanyPolicyDto> findByCompanyId(UUID companyId) {
        if (awsS3BucketEnabled) {
            String filePath = String.format(S_S_S, folderName, companyId, POLICIES);
            List<String> files = fileService.findFilesInFolder(filePath);
            List<CompanyPolicyDto> companyPolicyDtos = new ArrayList<>();
            files.forEach(file -> {
                String fileName = StringUtils.substringAfterLast(file, "/");
                String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(String.format("/lnf/company/%s/policies/%s", companyId, fileName))
                        .toUriString();
                CompanyPolicyDto companyPolicyDto = new CompanyPolicyDto();
                companyPolicyDto.setName(fileName);
                companyPolicyDto.setUrl(downloadURL);
                companyPolicyDtos.add(companyPolicyDto);
            });
            return companyPolicyDtos;
        } else {
            searchForCompany(companyId);
            List<CompanyPolicy> entities = repository.findByCompanyId(companyId);
            List<CompanyPolicyDto> companyPolicyDtos = entities.stream()
                    .map(CompanyPolicyConverter::toTransportModel)
                    .filter(Objects::nonNull)
                    .toList();
            companyPolicyDtos.forEach(f -> {
                String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(String.format("/lnf/company/%s/policies/", companyId))
                        .path(f.getId().toString())
                        .toUriString();
                f.setUrl(downloadURL);
            });
            return companyPolicyDtos;
        }
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID policyId, String fileName) {
        try {
            if (awsS3BucketEnabled) {
                String filePath = String.format("%s/%s/%s/%s", folderName, companyId, POLICIES, fileName);
                return fileService.findFileContent(filePath);
            } else {
                searchForCompany(companyId);
                CompanyPolicy companyPolicy = searchForPolicy(policyId);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                + companyPolicy.getName() + "\"")
                        .contentType(MediaType.valueOf(companyPolicy.getContentType()))
                        .body(companyPolicy.getContent());
            }
        } catch (RuntimeException e) {
            String errorMessage = String.format("file not found for Company[%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile[] policies) {
        Company company = searchForCompany(companyId);
        for (MultipartFile policy : policies) {
            try {
                LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                        String.format("Failed to create Policy for company [%s] with null payload", companyId));
                if (awsS3BucketEnabled) {
                    String folder = String.format(S_S_S, folderName, companyId, POLICIES);
                    String filePath = uploadFile(folder, policy);
                    log.debug("File uploaded successfully to S3 bucket: " + filePath);
                } else {
                    CompanyPolicy entity = CompanyPolicyConverter.toEntityModel(policy, false);
                    entity.setCompany(company);
                    save(entity);
                    log.debug("Policy {} for Company {} successfully created",
                            policy.getOriginalFilename(), companyId);
                }
            } catch (RuntimeException | IOException e) {
                String errorMessage = String.format("Failed to create policy[%s] for company [%s]",
                        companyId, policy.getOriginalFilename());
                throw new LnFException(errorMessage, e);
            }
        }
    }

    public void update(UUID companyId, UUID fileId, MultipartFile policy) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                String.format("Failed to update policy for company [%s] with null payload", companyId));
        try {
            if (awsS3BucketEnabled) {
                String folder = String.format(S_S_S, folderName, companyId, POLICIES);
                String filePath = uploadFile(folder, policy);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } else {
                searchForCompany(companyId);
                CompanyPolicy entity = searchForPolicy(fileId);
                CompanyPolicy updatedEntity = CompanyPolicyConverter.toEntityModel(policy, entity, awsS3BucketEnabled);
                save(updatedEntity);
                log.debug("Policy {} for Company {} successfully updated", fileId, companyId);
            }
        } catch (RuntimeException | IOException e) {
            String errorMessage = String.format("Failed to update policy[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPolicy> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete policies for company [%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteById(UUID companyId, UUID fileId, String fileName) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = String.format("%s/%s/%s/%s", folderName, companyId, POLICIES, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            log.debug("S3 object deleted for company");
        } else {
            searchForCompany(companyId);
            CompanyPolicy entity = searchForPolicy(fileId);
            try {
                repository.delete(entity);
                log.debug("Policy {} for company {} successfully deleted", fileId, companyId);
            } catch (RuntimeException e) {
                String errorMessage = String.format("Failed to delete Policy[[%s] for company [%s]", fileId, companyId);
                throw new LnFException(errorMessage);
            }
        }
    }

    private void save(CompanyPolicy entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Policy for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(
                        String.format("Company with id [%s] does not exist", companyId)));
    }

    private CompanyPolicy searchForPolicy(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException(
                        String.format("Company policy with id [%s] does not exist", fileId)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
