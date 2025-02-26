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

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyEventConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyEvent;
import com.lnf.company.repository.CompanyEventRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.CompanyEventDto;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class CompanyEventService implements PaginatedAndSortedService<CompanyEventDto> {
    private static final String FAILED_TO_CREATE_COMPANY_EVENT_NULL_PAYLOAD = "Failed to create companyEvent for " +
            "company [%s] with null payload";

    private final CompanyEventRepository companyEventRepository;
    private final CompanyRepository companyRepository;

    @Override
    public Page<CompanyEventDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyEventDto> findAll() {
        List<CompanyEvent> entities = companyEventRepository.findAll();
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Override
    public Page<CompanyEventDto> findPaginated(int page, int size) {
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyEventDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyEvent> entities = Lists.newArrayList(companyEventRepository.findAll(sortInfo));
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    private Page<CompanyEventDto> validateAndGetPages(int page, Page<CompanyEvent> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " + "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyEventConverter::toTransportModel);
    }

    public List<CompanyEventDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyEvent> entities = companyEventRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    public void create(UUID companyId, CompanyEventDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, FAILED_TO_CREATE_COMPANY_EVENT_NULL_PAYLOAD.formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyEvent entity = CompanyEventConverter.toEntityModel(resource, new CompanyEvent());
        entity.setCompany(companyEntity);
        save(entity);
        log.debug("CompanyEvent for Company {} successfully created", companyId);
    }


    public void create(UUID companyId, List<CompanyEventDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, FAILED_TO_CREATE_COMPANY_EVENT_NULL_PAYLOAD.formatted(companyId));
        Company company = searchForCompany(companyId);
        List<CompanyEvent> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(companyEventDto -> {
            CompanyEvent entity = CompanyEventConverter.toEntityModel(companyEventDto, new CompanyEvent());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.debug("companyEvents for Company {} successfully created", companyId);
    }

    private void save(CompanyEvent entity) {
        try {
            companyEventRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyEvent for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyEvent> entities) {
        try {
            companyEventRepository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save companyEvent for company [%s]", entities.getFirst().getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private CompanyEvent searchForCompanyEvent(UUID eventId) {
        return companyEventRepository.findById(eventId).orElseThrow(() -> new LnFEntityNotFoundException("companyEvent with id [%s] does not exist".formatted(eventId)));
    }

    public CompanyEventDto findById(UUID companyId, UUID eventId) {
        searchForCompany(companyId);
        return CompanyEventConverter.toTransportModel(searchForCompanyEvent(eventId));
    }

    public void update(UUID companyId, UUID eventId, CompanyEventDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, FAILED_TO_CREATE_COMPANY_EVENT_NULL_PAYLOAD.formatted(companyId));
        searchForCompany(companyId);
        CompanyEvent entity = searchForCompanyEvent(eventId);
        save(CompanyEventConverter.toEntityModel(resource, entity));
        log.debug("companyEvent for Company {} successfully updated", companyId);
    }


    public void deleteById(UUID companyId, UUID eventId) {
        searchForCompany(companyId);
        CompanyEvent entity = searchForCompanyEvent(eventId);
        try {
            companyEventRepository.delete(entity);
            log.debug("CompanyEvent {} for company {} successfully deleted", eventId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyEvent[[%s] for company [%s]".formatted(eventId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyEvent> entities = companyEventRepository.findByCompanyId(companyId);
        try {
            companyEventRepository.deleteAll(entities);
            log.debug("CompanyEvent for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete event for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

}
