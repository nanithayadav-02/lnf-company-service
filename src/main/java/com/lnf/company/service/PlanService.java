package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.PlanConverter;
import com.lnf.company.model.Plan;
import com.lnf.company.repository.PlanRepository;
import com.lnf.dto.company.PlanDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.tenant.core.context.TenantContext;
import com.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlanService implements PaginatedAndSortedService<PlanDto> {

    private final PlanRepository repository;
    private final CacheManager cacheManager;

    @Value("${lnf.tenant.enabled:true}")
    private boolean tenantEnabled;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<PlanDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<Plan> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<PlanDto> findPaginated(int page, int size) {
        Page<Plan> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<PlanDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<Plan> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(PlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<PlanDto> findAll() {
        return repository.findAll().stream().
                map(PlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(List<PlanDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create LnfPlan with null payload");
        List<Plan> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(planDto -> {
            Plan entity = PlanConverter.toEntityModel(planDto, new Plan());
            entities.add(entity);
        });
        save(entities);
        log.debug("LnfPlans successfully created");
    }

    public void create(PlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create LnfPlan with null payload");
        Plan entity = PlanConverter.toEntityModel(resource, new Plan());
        save(entity);
        log.debug("LnfPlan successfully created");
    }


    private void save(List<Plan> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to save LnfPlans";
            throw new LnFException(errorMessage);
        }
    }

    private void save(Plan entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to save LnfPlan";
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID planId, PlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update LnfPlan with null payload");
        Plan entity = searchForLnfPlan(planId);
        save(PlanConverter.toEntityModel(resource, entity));
        log.debug("LnfPlan for Id {} successfully created", planId);
    }

    private Plan searchForLnfPlan(UUID planId) {
        return repository.findById(planId).
                orElseThrow(() -> new LnFEntityNotFoundException("LnfPlan with id [%s] does not exist".formatted(planId)));
    }

    @CacheEvict(value = "company", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#planId)")
    public void deleteById(UUID planId) {
        Plan entity = searchForLnfPlan(planId);
        try {
            repository.delete(entity);
            log.debug("LnfPlan {} successfully deleted", planId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete LnfPlan[%s]".formatted(planId);
            throw new LnFException(errorMessage);
        }
    }

    @Cacheable(value = "company", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#planId)")
    public PlanDto findById(UUID planId) {
        return PlanConverter.toTransportModel(searchForLnfPlan(planId));
    }

    private Page<PlanDto> validateAndGetPages(int page, Page<Plan> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(PlanConverter::toTransportModel);
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