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
import com.lnf.company.converter.CompanyConverter;
import com.lnf.company.model.Company;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.dto.company.CompanyDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.service.specification.GenericSpecificationBuilder;
import com.lnf.tenant.core.context.TenantContext;
import com.lnf.util.RestUtil;
import com.lnf.util.specification.SpecificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyService implements PaginatedAndSortedService<CompanyDto> {

    private final CompanyRepository repository;
    private final ImageService imageService;
    private final CacheManager cacheManager;

    private final ApplicationContext applicationContext;

    @Value("${lnf.tenant.enabled:true}")
    private boolean tenantEnabled;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<CompanyDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<Company> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyDto> findPaginated(int page, int size) {
        Page<Company> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<Company> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(CompanyConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Override
    public List<CompanyDto> findAll() {
        List<Company> entities = repository.findAll();
        return entities.stream().map(CompanyConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    public List<CompanyDto> findAll(String search) {
        Specification<Company> specification = buildCompanySpecification(search);
        List<Company> entities = repository.findAll(specification);
        return convertToDtos(entities);
    }

    private List<CompanyDto> convertToDtos(List<Company> entities) {
        return entities.stream()
                .map(this::findCompanyWithImage)
                .filter(Objects::nonNull)
                .toList();
    }

    private Specification<Company> buildCompanySpecification(String search) {
        GenericSpecificationBuilder<Company> companySpecBuilder = new GenericSpecificationBuilder<>();
        Function<String, Class<?>> fieldClassForCompany = this::getFieldClassFromCompany;
        return SpecificationUtil.buildSpecification(search, companySpecBuilder, fieldClassForCompany);
    }

    private Class<?> getFieldClassFromCompany(String fieldName) {
        return SpecificationUtil.getFieldClass(Company.class, fieldName);
    }

    @Cacheable(value = "company", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#companyCode)")
    public CompanyDto findByCompanyCode(String companyCode) {
        Company entity = search(companyCode);
        return findCompanyWithImage(entity);
    }

    private CompanyDto findCompanyWithImage(Company entity) {
        CompanyDto dto = CompanyConverter.toTransportModel(entity);
        if (dto != null) {
            dto.setImage(imageService.findByCompanyId(dto.getId()));
        }
        return dto;
    }

    public void create(CompanyDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create Company with null payload");
        Company entity = CompanyConverter.toEntityModel(resource);
        saveEntity(entity);
        if (resource.getCode() != null) {
            applicationContext.getBean(this.getClass()).findByCode(resource.getCode());
        }
        log.debug("Company {} successfully created", entity.getCode());
    }

    @Transactional
    public void update(UUID companyId, CompanyDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to update Company with null payload");
        Company entity = search(companyId);
        Company updatedEntity = CompanyConverter.toEntityModel(resource);
        updatedEntity.setId(entity.getId());
        saveEntity(updatedEntity);

        if (resource.getCode() != null) {
            applicationContext.getBean(this.getClass()).findByCode(resource.getCode());
        }
        if (entity.getCode() != null && !Objects.equals(resource.getCode(), entity.getCode())) {
            applicationContext.getBean(this.getClass()).findByCode(entity.getCode());
        }

        log.debug("Company {} successfully updated", companyId);
    }

    @CachePut(value = "company", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#companyCode)")
    public CompanyDto findByCode(String companyCode) {
        Company entity = search(companyCode);
        return findCompanyWithImage(entity);
    }

    @CacheEvict(value = "company", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#companyId)")
    public void delete(UUID companyId) {
        Company entity = search(companyId);
        try {
            repository.delete(entity);
            log.debug("Company {} successfully deleted", entity.getCode());
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Company [%s]", entity.getCode());
            throw new LnFException(errorMessage, e);
        }
    }

    private Page<CompanyDto> validateAndGetPages(int page, Page<Company> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyConverter::toTransportModel);
    }

    private void saveEntity(Company entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save company [%s]", entity.getCode());
            throw new LnFException(errorMessage, e);
        }
    }

    private Company search(UUID companyId) {
        return repository.findById(companyId).orElseThrow(() -> entityNotFoundException(companyId));
    }

    private Company search(String companyCode) {
        return repository.findByCompanyCode(companyCode).orElseThrow(() -> entityNotFoundException(companyCode));
    }

    private LnFEntityNotFoundException entityNotFoundException(Object companyIdentifier) {
        return new LnFEntityNotFoundException("Company with id/code [%s] does not exist".formatted(companyIdentifier));
    }

    public void clearCaches() {
        String tenantId = TenantContext.getCurrentTenant();

        // If tenant mode is disabled or Redis is not used, clear in-memory caches
        if (!tenantEnabled || !(cacheManager instanceof RedisCacheManager)) {
            log.info("Clearing all in-memory caches (Redis disabled or tenant mode off).");
            cacheManager.getCacheNames().forEach(name -> {
                Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                }
            });
            return;
        }

        // Skip Redis cache clearing if tenant context is missing
        if (!StringUtils.hasText(tenantId)) {
            log.warn("TenantContext is not set. Skipping Redis cache clearing.");
            return;
        }

        log.info("Clearing Redis cache for tenant '{}'", tenantId);

        String pattern = "*::" + tenantId + ":*";

        Set<String> keysToDelete = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(1000).build())) {
                cursor.forEachRemaining(key -> keys.add(new String(key, StandardCharsets.UTF_8)));
            }
            return keys;
        });

        if (!CollectionUtils.isEmpty(keysToDelete)) {
            redisTemplate.delete(keysToDelete);
            log.info("Deleted {} Redis keys for tenant '{}'", keysToDelete.size(), tenantId);
        } else {
            log.info("No Redis keys found for tenant '{}'", tenantId);
        }
    }

}
