package com.technofacts.lnf.company.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.dto.company.CompanyDto;
import com.technofacts.lnf.service.File.FileUploadService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log
@RequiredArgsConstructor
public class SalaryConfigurationService {

    private final FileUploadService fileUploadService;

    private final CompanyService service;


    private final CacheManager cacheManager;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.financialYear}")
    private String financialYear;

    @Value("${aws.s3.bucket.companyCode}")
    private String companyCode;

    @Value("${aws.s3.bucket.salaryConfigFile}")
    private String salaryConfigFile;

    @CacheEvict(value = "salaryConfiguration", allEntries = true)
    public void reloadCacheBySalaryConfigurations() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        retrieveSalaryConfigurations();
    }

    @Cacheable(value = "salaryConfiguration")
    public JsonNode retrieveSalaryConfigurations() {
        try {
            if (awsS3BucketEnabled) {
                CompanyDto companyDto = service.findByCompanyCode(companyCode);
                UUID companyId = companyDto.getId();
                String filePath = folderName + "/"
                        + companyId + "/payroll/salary-configuration/"
                        + financialYear + "/" + salaryConfigFile;
                ResponseEntity<byte[]> s3Response = fileUploadService.findFile(filePath);
                if (s3Response.getStatusCode() == HttpStatus.OK) {
                    byte[] fileContent = s3Response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readTree(fileContent);
                }
            }
                Resource resource = new ClassPathResource("SalaryConfigurations.json");

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonData = objectMapper.readTree(resource.getInputStream());
                JsonNode salaryComponents = jsonData.get("salary_components");
                JsonNode salaryDeductions = jsonData.get("salary_deductions");
                ObjectMapper newObjectMapper = new ObjectMapper();
                ObjectNode desiredJson = newObjectMapper.createObjectNode();
                desiredJson.set("salary_components", salaryComponents);
                desiredJson.set("salary_deductions", salaryDeductions);

                return desiredJson;

        } catch (Exception e) {
            throw new LnFException("Failed to retrieve salary configurations", e);
        }
    }

    public String create(UUID companyId, String financialYear, MultipartFile file) {
        if (awsS3BucketEnabled) {
            String folder = folderName + "/" + companyId + "/payroll/salary-configuration/" + financialYear + "/";
            return fileUploadService.uploadFile(folder,file);
        }
        return "File upload to AWS S3 bucket is not enabled";
    }

    public void deleteByCompanyId(UUID companyId , String financialYear) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = folderName + "/"
                    + companyId + "/payroll/salary-configuration/"
                    + financialYear + "/" + salaryConfigFile;
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileUploadService.delete(filePaths);
            log.info(() -> String.format("Salary configuration is deleted for the company [%s] and financial year " +
                    "[%s}]", companyId, financialYear));
        }
    }
}
