package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.CompanyPolicyConverter;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyPolicy;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.CompanyPolicyRepository;
import com.technofacts.lnf.dto.company.PolicyDto;
import com.technofacts.lnf.exception.LnFBadRequestException;
import com.technofacts.lnf.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.exception.LnFException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class CompanyPolicyService {

    private final CompanyRepository companyRepository;
    private final CompanyPolicyRepository repository;

    public List<PolicyDto> findAll() {
        List<CompanyPolicy> entities = repository.findAll();
        return entities.stream().map(CompanyPolicyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<PolicyDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPolicy> entities = repository.findByCompanyId(companyId);
        List<PolicyDto> fileDtos = entities.stream()
                .map(CompanyPolicyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        fileDtos.forEach(f -> {
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/employees/%s/files/", companyId))
                    .path(f.getId().toString())
                    .toUriString();
            f.setUrl(downloadURL);
        });
        return fileDtos;
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID imageId) {
        searchForCompany(companyId);
        CompanyPolicy companyPolicy = searchForFile(imageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + companyPolicy.getName() + "\"")
                .contentType(MediaType.valueOf(companyPolicy.getContentType()))
                .body(companyPolicy.getContent());
    }

    public void create(UUID companyId, MultipartFile[] files) {
        Company employee = searchForCompany(companyId);
        for (MultipartFile file : files) {
            try {
                LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                        String.format("Failed to create File for employee [%s] with null payload", companyId));
                CompanyPolicy entity = CompanyPolicyConverter.toEntityModel(file);
                entity.setCompany(employee);
                save(entity);
                log.info(() -> String.format("File [%s] for Company[%s] successfully created", file.getOriginalFilename(), companyId));

            } catch (RuntimeException | IOException e) {
                String errorMessage = String.format("Failed to create file[%s] for employee [%s]", companyId, file.getOriginalFilename());
                throw new LnFException(errorMessage, e);
            }
        }
    }

    public void update(UUID companyId, UUID fileId, MultipartFile file) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, file,
                String.format("Failed to update file for employee [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyPolicy entity = searchForFile(fileId);
        try {
            CompanyPolicy updatedEntity = CompanyPolicyConverter.toEntityModel(file, entity);
            save(updatedEntity);
        } catch (RuntimeException | IOException e) {
            String errorMessage = String.format("Failed to update file[%s] for employee [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
        log.info(() -> String.format("File [%s] for Company[%s] successfully updated", fileId, companyId));
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPolicy> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete files for employee [%s]", companyId);
            throw new LnFException(errorMessage, e);
        }
    }

    public void deleteById(UUID companyId, UUID fileId) {
        searchForCompany(companyId);
        CompanyPolicy entity = searchForFile(fileId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("File[%s] for employee [%s] successfully deleted", fileId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete File[[%s] for employee [%s]", fileId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyPolicy entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save File for employee [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private CompanyPolicy searchForFile(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Image with id [%s] does not exist", fileId)));
    }
}
