package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.CompanyFileConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyFile;
import com.technofacts.lnf.company.repository.CompanyFileRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.CompanyFileDto;
import com.technofacts.lnf.dto.file.FileDto;
import com.technofacts.lnf.service.file.FileFolderService;
import com.technofacts.lnf.service.file.FileService;
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

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;
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
        String filePath = String.format(S_S_S, folderName, companyId, FILES);
        List<FileDto> files = fileFolderService.findFiles(filePath);
        List<CompanyFileDto> companyFileDtos = new ArrayList<>();
        files.forEach(file -> {
            String fileName = StringUtils.substringAfterLast(file.getFileName(), "/");
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/company/%s/files/%s", companyId, fileName))
                    .toUriString();
            CompanyFileDto companyFileDto = new CompanyFileDto();
            CompanyFile entity = searchForFileName(fileName);
            companyFileDto.setId(entity.getId());
            companyFileDto.setFileName(fileName);
            companyFileDto.setUrl(downloadURL);
            companyFileDto.setDescription(entity.getDescription());
            companyFileDto.setSize(file.getFileSize());
            companyFileDto.setLastModified(file.getLastModified());
            companyFileDtos.add(companyFileDto);
        });
        return companyFileDtos;
    }

    public ResponseEntity<byte[]> findById(UUID companyId, String fileName) {
        try {
            searchForFileName(fileName);
            String filePath = String.format("%s/%s/%s/%s", folderName, companyId, FILES, fileName);
            return fileService.findFileContent(filePath);
        } catch (RuntimeException e) {
            String errorMessage = String.format("file not found for Company[%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile[] files, List<CompanyFileDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format(FAILED_TO_CREATE_COMPANY_FILE_NULL_PAYLOAD, companyId));
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

                String folder = String.format(S_S_S, folderName, companyId, FILES);
                String filePath = uploadFile(folder, file);
                log.debug("File uploaded successfully to S3 bucket: " + filePath);
            } catch (RuntimeException e) {
                String errorMessage = String.format("Failed to create file[%s] for company [%s]", file.getName(), companyId);
                throw new LnFException(errorMessage, e);
            }
        });
    }

    public void update(UUID companyId, String fileName, MultipartFile file, CompanyFileDto resource) {
        com.technofacts.lnf.exception.LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                String.format("Failed to update file for company [%s] with null payload", companyId));
        try {
            CompanyFile entity = searchForFileName(fileName);
            CompanyFile updatedEntity = CompanyFileConverter.toEntityModel(resource, entity);
            save(updatedEntity);
            String s3ObjectKey = String.format("%s/%s/%s/%s", folderName, companyId, FILES, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            //Before Updating the file we are deleting from the s3 bucket
            String folder = String.format(S_S_S, folderName, companyId, FILES);
            String filePath = uploadFile(folder, file);
            log.debug("File uploaded successfully to S3 bucket: " + filePath);
            log.debug("fileName {} for Company {} successfully updated", fileName, companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to update fileName[%s] for company [%s]", fileName, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByIdAndFileName(UUID companyId, String fileName) {
        searchForCompany(companyId);
        CompanyFile entity = searchForFileName(fileName);
        try {
            String s3ObjectKey = String.format("%s/%s/%s/%s", folderName, companyId, FILES, fileName);
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            fileService.delete(filePaths);
            log.debug("S3 object deleted for company file");
            repository.delete(entity);
            log.debug("file {} for company {} successfully deleted", fileName, companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete File[[%s] for company [%s]", fileName, companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyFile> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyFile for company [%s]", entities.get(0).getCompany().getId());
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
                .orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private CompanyFile searchForFileName(String fileName) {
        return repository.findByFileName(fileName).
                orElseThrow(() -> new LnFEntityNotFoundException(
                        String.format("Company file with fileName [%s] does not exist", fileName)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
