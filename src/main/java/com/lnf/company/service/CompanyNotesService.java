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
import com.lnf.company.converter.CompanyNotesConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyNotes;
import com.lnf.company.repository.CompanyNotesRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.NotesDto;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyNotesService implements PaginatedAndSortedService<NotesDto> {

    private final CompanyRepository companyRepository;
    private final CompanyNotesRepository companyNotesRepository;
    private final CacheManager cacheManager;

    @Override
    public Page<NotesDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyNotes> resultPage = companyNotesRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<NotesDto> findPaginated(int page, int size) {
        Page<CompanyNotes> resultPage = companyNotesRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<NotesDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyNotes> entities = Lists.newArrayList(companyNotesRepository.findAll(sortInfo));
        return entities.stream().map(CompanyNotesConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<NotesDto> findAll() {
        return companyNotesRepository.findAll().stream().
                map(CompanyNotesConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyId, List<NotesDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create notes for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        List<CompanyNotes> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(notesDto -> {
            CompanyNotes entity = CompanyNotesConverter.toEntityModel(notesDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.debug("Notes for company {} successfully created", companyId);
    }

    public void create(UUID companyId, NotesDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create notes for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyNotes entity = CompanyNotesConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.debug("notes for company {} successfully created", companyId);
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    private void save(List<CompanyNotes> entities) {
        try {
            companyNotesRepository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save notes for company [%s]", entities.getFirst().getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyNotes entity) {
        try {
            companyNotesRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save notes for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyId, UUID notesId, NotesDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update Notes with null payload");
        searchForCompany(companyId);
        CompanyNotes entity = searchForNotes(notesId);
        entity.setNotes(resource.getNotes());
        save(CompanyNotesConverter.toEntityModel(resource, entity));
        log.debug("Notes for Employee {} successfully created", notesId);
    }

    private CompanyNotes searchForNotes(UUID notesId) {
        return companyNotesRepository.findById(notesId).
                orElseThrow(() -> new LnFEntityNotFoundException("notes with id [%s] does not exist".formatted(notesId)));
    }
    @CacheEvict(value="companyNotes" ,key = "#notesId")
    public void deleteById(UUID companyId, UUID notesId) {
        searchForCompany(companyId);
        CompanyNotes entity = searchForNotes(notesId);
        try {
            companyNotesRepository.delete(entity);
            log.debug("Notes {} for company {} successfully deleted", notesId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete Notes[%s] for company [%s]".formatted(notesId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    @CacheEvict(value="companyNotes" ,key = "#companyId")
    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyNotes> entities = companyNotesRepository.findByCompanyId(companyId);
        try {
            companyNotesRepository.deleteAll(entities);
            log.debug("Notes for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete notes for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

    @Cacheable(value="companyNotes" ,key = "#companyId")
    public List<NotesDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyNotes> entities = companyNotesRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyNotesConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Cacheable(value="companyNotes" ,key = "#notesId")
    public NotesDto findById(UUID companyId, UUID notesId) {
        searchForCompany(companyId);
        return CompanyNotesConverter.toTransportModel(searchForNotes(notesId));
    }

    private Page<NotesDto> validateAndGetPages(int page, Page<CompanyNotes> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyNotesConverter::toTransportModel);
    }

    public void clearCaches() {
        Objects.requireNonNull(cacheManager.getCache("companyNotes")).clear();
        log.debug("companyNotes cache cleared.");
    }

}
