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

    public List<CompanyPolicyDto> findAll() {
        List<CompanyPolicy> entities = repository.findAll();
        return entities.stream().map(CompanyPolicyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CompanyPolicyDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPolicy> entities = repository.findByCompanyId(companyId);
        List<CompanyPolicyDto> companyPolicyDtos = entities.stream()
                .map(CompanyPolicyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        companyPolicyDtos.forEach(f -> {
            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(String.format("/lnf/company/%s/policies/", companyId))
                    .path(f.getId().toString())
                    .toUriString();
            f.setUrl(downloadURL);
        });
        return companyPolicyDtos;
    }

    public ResponseEntity<byte[]> findById(UUID companyId, UUID policyId) {
        searchForCompany(companyId);
        CompanyPolicy companyPolicy = searchForPolicy(policyId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + companyPolicy.getName() + "\"")
                .contentType(MediaType.valueOf(companyPolicy.getContentType()))
                .body(companyPolicy.getContent());
    }

    public void create(UUID companyId, MultipartFile[] policies) {
        Company company = searchForCompany(companyId);
        for (MultipartFile policy : policies) {
            try {
                LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                        String.format("Failed to create Policy for company [%s] with null payload", companyId));
                CompanyPolicy entity = CompanyPolicyConverter.toEntityModel(policy);
                entity.setCompany(company);
                save(entity);
                log.info(() -> String.format("Policy [%s] for Company[%s] successfully created", policy.getOriginalFilename(), companyId));

            } catch (RuntimeException | IOException e) {
                String errorMessage = String.format("Failed to create policy[%s] for company [%s]", companyId, policy.getOriginalFilename());
                throw new LnFException(errorMessage, e);
            }
        }
    }

    public void update(UUID companyId, UUID fileId, MultipartFile policy) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, policy,
                String.format("Failed to update policy for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyPolicy entity = searchForPolicy(fileId);
        try {
            CompanyPolicy updatedEntity = CompanyPolicyConverter.toEntityModel(policy, entity);
            save(updatedEntity);
        } catch (RuntimeException | IOException e) {
            String errorMessage = String.format("Failed to update policy[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage, e);
        }
        log.info(() -> String.format("Policy [%s] for Company[%s] successfully updated", fileId, companyId));
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

    public void deleteById(UUID companyId, UUID fileId) {
        searchForCompany(companyId);
        CompanyPolicy entity = searchForPolicy(fileId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Policy[%s] for company [%s] successfully deleted", fileId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Policy[[%s] for company [%s]", fileId, companyId);
            throw new LnFException(errorMessage);
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
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private CompanyPolicy searchForPolicy(UUID fileId) {
        return repository.findById(fileId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company policy with id [%s] does not exist", fileId)));
    }
}
