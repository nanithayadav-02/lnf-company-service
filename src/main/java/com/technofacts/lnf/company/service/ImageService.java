package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.ImageConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.Image;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.ImageRepository;
import com.technofacts.lnf.dto.company.ImageDto;
import com.technofacts.lnf.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Paths;
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

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    public ImageDto findByCompanyId(UUID companyId) {
        if (awsS3BucketEnabled) {
            String filePath = String.format(S_S_S, folderName, companyId, IMAGE);
            List<String> filePaths = fileService.findFilesInFolder(filePath);
            if (filePaths == null || filePaths.isEmpty()) {
                log.debug("Image not found for company {}", companyId);
                return null;
            }
            String fileName = Paths.get(filePaths.get(0)).getFileName().toString();
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/company/%s/image/%s", companyId, fileName))
                    .toUriString();

            ImageDto imageDto = new ImageDto();
            imageDto.setName(fileName);
            imageDto.setUrl(url);

            return imageDto;
        } else {
            searchForCompany(companyId);
            Image entity = repository.findByCompanyId(companyId);
            if (entity == null) {
                throw new LnFEntityNotFoundException(String.format("Image for company [%s] does not exist", companyId));
            }

            ImageDto imageDto = ImageConverter.toTransportModel(entity);
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/company/%s/image/", companyId))
                    .path(imageDto.getId().toString())
                    .toUriString();
            imageDto.setUrl(downloadURL);

            return imageDto;
        }
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID imageId, String fileName) {
        try {
            if (awsS3BucketEnabled) {
                String filePath = String.format("%s/%s/%s/%s", folderName, companyId, IMAGE, fileName);
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
            String errorMessage = String.format("file not found for Company[%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void create(UUID companyId, MultipartFile file) {
        try {
            LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                    String.format("Failed to create Image for company [%s] with null payload", companyId));
            if (awsS3BucketEnabled) {
                String folder = String.format(S_S_S, folderName, companyId, IMAGE);
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

            String errorMessage = String.format("Failed to create image[%s] for company [%s]", companyId,
                    file.getOriginalFilename());
            throw new LnFException(errorMessage, e);
        }
    }

    public void update(UUID companyId, UUID fileId, MultipartFile file) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                String.format("Failed to update file for company [%s] with null payload", companyId));
        try {
            if (awsS3BucketEnabled) {
                String folder = String.format(S_S_S, folderName, companyId, IMAGE);
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
            String errorMessage = String.format("Failed to update file[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        Image entity = repository.findByCompanyId(companyId);
        try {
            repository.delete(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete image for company [%s]", companyId);
            throw new LnFException(errorMessage, e);
        }

    }

    public void deleteById(UUID companyId, UUID fileId, String fileName) {
        if (awsS3BucketEnabled) {
            String s3ObjectKey = String.format("%s/%s/%s/%s", folderName, companyId, IMAGE, fileName);
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
                String errorMessage = String.format("Failed to delete Image[[%s] for company [%s]", fileId, companyId);
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
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private Image searchForImage(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Image with id [%s] does not exist",
                        fileId)));
    }

    private String uploadFile(String folder, MultipartFile file) {
        return fileService.uploadFile(folder, file);
    }

}
