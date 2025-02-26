/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.service;

import com.lnf.company.converter.GstConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyGst;
import com.lnf.company.repository.CompanyGstRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.GstDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyGstService {

    private final CompanyGstRepository repository;
    private final CompanyRepository companyRepository;

    public List<GstDto> findAll() {
        List<CompanyGst> entities = repository.findAll();
        return entities.stream().map(GstConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<GstDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyGst> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(GstConverter::toTransportModel).filter(Objects::nonNull)
                .toList();
    }

    public GstDto findById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        return GstConverter.toTransportModel(searchForGst(gstId));
    }

    public void create(UUID companyId, List<GstDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create gst for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        List<CompanyGst> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(gstDto -> {
            CompanyGst entity = GstConverter.toEntityModel(gstDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.debug("Gst for company {} successfully created", companyId);
    }

    public void create(UUID companyId, GstDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create gst for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyGst entity = GstConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.debug("Gst for company {} successfully created", companyId);
    }

    public void update(UUID companyId, UUID gstId, GstDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to gst company[%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        searchForGst(gstId);
        CompanyGst updatedEntity = GstConverter.toEntityModel(resource);
        updatedEntity.setCompany(companyEntity);
        save(updatedEntity);
        log.debug("Gst for company {} successfully updated", companyId);
    }

    public void deleteById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        CompanyGst entity = searchForGst(gstId);
        try {
            repository.delete(entity);
            log.debug("Gst {} for company {} successfully deleted", gstId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete gst[%s] for company [%s]".formatted(gstId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyGst> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.debug("Gsts for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete gsts for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyGst entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save gst for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyGst> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save gst for company [%s]", entities.getFirst().getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    private CompanyGst searchForGst(UUID gstId) {
        return repository.findById(gstId).
                orElseThrow(() -> new LnFEntityNotFoundException("Gst with id [%s] does not exist".formatted(gstId)));
    }

}
