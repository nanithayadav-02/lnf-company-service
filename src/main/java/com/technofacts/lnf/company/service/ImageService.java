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
import com.technofacts.lnf.service.File.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class ImageService {

    private final CompanyRepository companyRepository;
    private final ImageRepository repository;

    private final FileUploadService fileUploadService;

    @Value("${aws.s3.bucket.enabled}")
    private boolean awsS3BucketEnabled;

    @Value("${aws.s3.bucket.folderName}")
    private String folderName;

    @Value("${aws.s3.bucket.fileName}")
    private String fileName;

    public List<ImageDto> findAll() {
        List<Image> entities = repository.findAll();
        return entities.stream().map(ImageConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public ImageDto findByCompanyId(UUID companyId) {

        if (awsS3BucketEnabled) {
            ResponseEntity<byte[]> s3Response = findFile(folderName + "/" + companyId +  "/" + fileName);
            if (s3Response.getStatusCode() == HttpStatus.OK) {
                ImageDto imageDto = new ImageDto();

                String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/lnf/file")
                        .queryParam("filePath", folderName + "/" + companyId +  "/" + fileName)
                        .toUriString();
                imageDto.setUrl(downloadURL);
                imageDto.setContentType("application/octet-stream");
                return imageDto;
            }
        } else {
            searchForCompany(companyId);
            List<Image> entities = repository.findByCompanyId(companyId);
            if (CollectionUtils.isEmpty(entities)) {
                throw new LnFEntityNotFoundException(String.format("Image for company [%s] does not exist", companyId));
            }
            ImageDto imageDto = ImageConverter.toTransportModel(entities.get(0));
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/company/%s/image/", companyId))
                    .path(imageDto.getId().toString())
                    .toUriString();
            imageDto.setUrl(downloadURL);

            return imageDto;
        }
        return null;
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID imageId) {
        searchForCompany(companyId);
        Image file = searchForImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.valueOf(file.getContentType()))
                .body(file.getContent());
    }

    public void create(UUID companyId, MultipartFile file) {
        Company company = searchForCompany(companyId);
        List<Image> entities = repository.findByCompanyId(companyId);

        try {
            LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                    String.format("Failed to create Image for company [%s] with null payload", companyId));
            String filePath = null;
            if (awsS3BucketEnabled) {
                String folder = folderName + "/" + companyId + "/";
                filePath = uploadFile(folder, file);
                log.info("File uploaded successfully to S3 bucket: " + filePath);
            } else {
                Image entity = ImageConverter.toEntityModel(file, false, null);
                if (entities.size() > 0) {
                    entity.setId(entities.get(0).getId());
                }
                entity.setCompany(company);
                save(entity);
                log.info(() -> String.format("Image [%s] for Company[%s] successfully created",
                        file.getOriginalFilename(), companyId));
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
        searchForCompany(companyId);
        Image entity = searchForImage(fileId);
        try {
            String filePath = null;
            if (awsS3BucketEnabled) {
                String folder = folderName + "/" + companyId + "/";
                filePath = uploadFile(folder, file);
                log.info("file uploaded successfully" + filePath);
            } else {
                Image updatedEntity = ImageConverter.toEntityModel(file, entity, false, null);
                save(updatedEntity);
                log.info(() -> String.format("Image [%s] for Company[%s] successfully updated", fileId, companyId));
            }
        } catch (RuntimeException | IOException e) {
            String errorMessage = String.format("Failed to update file[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteByCompanyId(UUID companyId) {

        if (awsS3BucketEnabled) {
            String s3ObjectKey = folderName +"/" + companyId + "/" +  fileName;
            List<String> filePaths = Collections.singletonList(s3ObjectKey);
            deleteObjects( filePaths);
            log.info("S3 object deleted for company");
        } else {
            searchForCompany(companyId);
            List<Image> entities = repository.findByCompanyId(companyId);
            try {
                repository.deleteAll(entities);
            } catch (RuntimeException e) {
                String errorMessage = String.format("Failed to delete image for company [%s]", companyId);
                throw new LnFException(errorMessage, e);
            }
        }
    }

    public void deleteById(UUID companyId, UUID fileId) {
        searchForCompany(companyId);
        Image entity = searchForImage(fileId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Image[%s] for company [%s] successfully deleted", fileId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Image[[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage);
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
        return fileUploadService.uploadFile(folder,file);
    }

    private void deleteObjects(List<String> filePaths) {
        fileUploadService.deleteObjects(filePaths);
    }

    public ResponseEntity<byte[]> findFile(String filePath) {
        return fileUploadService.findFile(filePath);
    }
}
