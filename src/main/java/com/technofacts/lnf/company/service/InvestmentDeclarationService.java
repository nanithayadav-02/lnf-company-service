package com.technofacts.lnf.company.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.dto.company.CompanyDto;
import com.technofacts.lnf.service.File.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
public class InvestmentDeclarationService {

    private final FileUploadService fileUploadService;

    private final CompanyService service;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.financialYear}")
    private String financialYear;

    @Value("${aws.s3.bucket.companyCode}")
    private String companyCode;

    public JsonNode retrieveInvestmentDeclarations() {
        try {
            if (awsS3BucketEnabled) {
                CompanyDto companyDto = service.findByCompanyCode(companyCode);
                UUID companyId = companyDto.getId();
                ResponseEntity<byte[]> s3Response = findFile(folderName + "/" + companyId + "/payroll/investment-declaration/" + financialYear + "/" + "Investment_declaration.json");
                if (s3Response.getStatusCode() == HttpStatus.OK) {
                    byte[] fileContent = s3Response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readTree(fileContent);
                }
            }
        } catch (Exception e) {
            throw new LnFException("Failed to retrieve salary configurations", e);
        }
        return null;
    }

    public String create(UUID companyId , String financialYear, MultipartFile file) {
        if (awsS3BucketEnabled) {
            String folder = folderName + "/" + companyId + "/payroll/investment-declaration/" + financialYear + "/";
            return uploadFile(folder, file);
        }
        return "File upload to s3 is failed";
    }

    public String findByCompanyId(UUID companyId , String financialYear) {
        String filePath = folderName + "/" + companyId + "/payroll/salary-configuration/" + financialYear + "/" + "Investment_declaration.json";
        findFile(filePath);
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/lnf/company/payroll/salary-configuration")
                .queryParam("filePath", filePath)
                .toUriString();
        return downloadURL;
    }

    public void deleteByCompanyId(UUID companyId , String financialYear) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = folderName + "/" + companyId + "/payroll/investment-declaration/" + financialYear + "/" + "Investment_declaration.json";
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

    private  ResponseEntity<byte[]> findFile(String filePath) {
        return fileUploadService.findFile(filePath);
    }
}
