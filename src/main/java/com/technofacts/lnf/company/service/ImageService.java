package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.ImageConverter;
import com.technofacts.lnf.company.dto.ImageDto;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.Image;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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

    public List<ImageDto> findAll() {
        List<Image> entities = repository.findAll();
        return entities.stream().map(ImageConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public ImageDto findByCompanyId(String companyId) {
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

    public ResponseEntity<byte[]> findById(String companyId, UUID imageId) {
        searchForCompany(companyId);
        Image file = searchForImage(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.valueOf(file.getContentType()))
                .body(file.getContent());
    }

    public void create(String companyId, MultipartFile file) {
        Company company = searchForCompany(companyId);
        List<Image> entities = repository.findByCompanyId(companyId);

        try {
            LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                    String.format("Failed to create Image for company [%s] with null payload", companyId));
            Image entity = ImageConverter.toEntityModel(file);
            if (entities.size() > 0) {
                entity.setId(entities.get(0).getId());
            }
            entity.setCompany(company);
            save(entity);
            log.info(() -> String.format("Image [%s] for Company[%s] successfully created",
                    file.getOriginalFilename(), companyId));

        } catch (RuntimeException | IOException e) {

            String errorMessage = String.format("Failed to create image[%s] for company [%s]", companyId,
                    file.getOriginalFilename());
            throw new LnFException(errorMessage, e);
        }

    }

    public void update(String companyId, UUID fileId, MultipartFile file) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                String.format("Failed to update file for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        Image entity = searchForImage(fileId);
        try {
            Image updatedEntity = ImageConverter.toEntityModel(file, entity);
            save(updatedEntity);
        } catch (RuntimeException | IOException e) {
            String errorMessage = String.format("Failed to update file[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
        log.info(() -> String.format("Image [%s] for Company[%s] successfully updated", fileId, companyId));
    }

    public void deleteByCompanyId(String companyId) {
        searchForCompany(companyId);
        List<Image> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete image for company [%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteById(String companyId, UUID fileId) {
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
                    entity.getCompany().getCompanyId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(String companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private Image searchForImage(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Image with id [%s] does not exist",
                        fileId)));
    }
}
