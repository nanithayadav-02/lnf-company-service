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

import com.lnf.company.converter.ThemeConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.Theme;
import com.lnf.company.repository.ThemeRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.ThemeDto;
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
public class ThemeService {

    private final ThemeRepository repository;
    private final CompanyRepository companyRepository;

    public List<ThemeDto> findAll() {
        List<Theme> entities = repository.findAll();
        return entities.stream().map(ThemeConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<ThemeDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Theme> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(ThemeConverter::toTransportModel).filter(Objects::nonNull)
                .toList();
    }

    public ThemeDto findById(UUID companyId, UUID themeId) {
        searchForCompany(companyId);
        return ThemeConverter.toTransportModel(searchForTheme(themeId));
    }

    public void create(UUID companyId, List<ThemeDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create theme for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        List<Theme> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(gstDto -> {
            Theme entity = ThemeConverter.toEntityModel(gstDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.debug("Theme for company {} successfully created", companyId);
    }

    public void create(UUID companyId, ThemeDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create theme for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        Theme entity = ThemeConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.debug("Theme for company {} successfully created", companyId);
    }

    public void update(UUID companyId, UUID themeId, ThemeDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to theme company[%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        searchForTheme(themeId);
        Theme updatedEntity = ThemeConverter.toEntityModel(resource);
        updatedEntity.setCompany(companyEntity);
        save(updatedEntity);
        log.debug("Theme for company {} successfully updated", companyId);
    }

    public void deleteById(UUID companyId, UUID themeId) {
        searchForCompany(companyId);
        Theme entity = searchForTheme(themeId);
        try {
            repository.delete(entity);
            log.debug("Theme {} for company {} successfully deleted", themeId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete theme[%s] for company [%s]", themeId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Theme> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.debug("Theme for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete gsts for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(Theme entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save theme for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<Theme> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save theme for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private Theme searchForTheme(UUID themeId) {
        return repository.findById(themeId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Theme with id [%s] does not exist", themeId)));
    }

}
