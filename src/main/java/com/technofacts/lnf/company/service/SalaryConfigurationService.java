package com.technofacts.lnf.company.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.service.File.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Log
@RequiredArgsConstructor
public class SalaryConfigurationService {

    private final FileUploadService fileUploadService;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.fileName}")
    private String fileName;

    public JsonNode retrieveSalaryConfigurations() {
        try {
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

    public String create(UUID companyId , String financialYear, MultipartFile file) {
        if (awsS3BucketEnabled) {
            String folder = folderName + "/" + companyId + "/payroll/salary-configuration/" + financialYear + "/";
            return uploadFile(folder, file);
        }
        return "file is not uploaded";
    }

    public String findByCompanyId(UUID companyId , String financialYear) {
        String filePath = folderName + "/" + companyId + "/payroll/salary-configuration/" + financialYear + "/" + "SalaryConfigurations.json";
        findFile(filePath);
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/lnf/company/payroll/salary-configuration")
                .queryParam("filePath", filePath)
                .toUriString();
        return downloadURL;
    }

    public void deleteByCompanyId(UUID companyId , String financialYear) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = folderName + "/" + companyId + "/payroll/salary-configuration/" + financialYear + "/" + fileName;
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            delete(filePaths);
            log.info("S3 object deleted for company");
        }
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileUploadService.uploadFile(folder,file);
    }

    private void delete(List<String> filePaths) {
        fileUploadService.delete(filePaths);
    }

    public ResponseEntity<byte[]> findFile(String filePath) {
        return fileUploadService.findFile(filePath);
    }
}
