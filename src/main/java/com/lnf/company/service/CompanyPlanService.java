package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyPlanConverter;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.Plan;
import com.lnf.company.model.enums.CompanyPlanStatus;
import com.lnf.company.repository.CompanyPlanRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.company.repository.PlanRepository;
import com.lnf.dto.company.CompanyPlanAuditDto;
import com.lnf.dto.company.CompanyPlanDto;
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
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyPlanService implements PaginatedAndSortedService<CompanyPlanDto> {

    private final CompanyRepository companyRepository;
    private final CompanyPlanRepository companyPlanRepository;
    private final PlanRepository lnfPlanRepository;
    private final CompanyPlanAuditService lnfPlanAuditService;
    private final CacheManager cacheManager;

    @Value("${lnf.tenant.enabled:true}")
    private boolean tenantEnabled;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public Page<CompanyPlanDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyPlan> resultPage = companyPlanRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyPlanDto> findPaginated(int page, int size) {
        Page<CompanyPlan> resultPage = companyPlanRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyPlanDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyPlan> entities = Lists.newArrayList(companyPlanRepository.findAll(sortInfo));
        return entities.stream().map(CompanyPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<CompanyPlanDto> findAll() {
        return companyPlanRepository.findAll().stream().
                map(CompanyPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyId, CompanyPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyPlan for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        Plan lnfPlan = searchForPlan(resource.getLnfPlanId());

        List<CompanyPlan> companyPlans = companyPlanRepository.findByCompanyId(companyId);

        if (companyPlans != null && !companyPlans.isEmpty()) {
            // Check if the plan already exists
            boolean matchFound = companyPlans.stream()
                    .anyMatch(plan -> plan.getPlan().getId().equals(resource.getLnfPlanId()));

            if (matchFound) {
                throw new LnFException("The specified plan is already active.");
            } else {
                // Get the plan with no end date
                CompanyPlan previousCompanyPlan = companyPlans.stream()
                        .filter(companyPlan -> companyPlan.getEndDate() == null)
                        .findFirst()
                        .orElseThrow(() -> new LnFException("No active plan found."));

                LocalDate startDate = previousCompanyPlan.getStartDate();

                if (!startDate.isBefore(resource.getStartDate())) {
                    throw new LnFException("The provided start date cannot be the same as the existing plan's start date or any previous date.");
                }

                companyPlans.stream()
                        .filter(companyPlan -> companyPlan.getEndDate() == null)
                        .forEach(plan -> {
                            CompanyPlanDto planDto = new CompanyPlanDto();
                            planDto.setCompanyId(companyId);
                            planDto.setId(plan.getId());
                            planDto.setLnfPlanId(plan.getPlan().getId());
                            planDto.setStatus(CompanyPlanStatus.CANCELLED.name());
                            planDto.setStartDate(plan.getStartDate());
                            planDto.setEndDate(resource.getStartDate().minusDays(1));

                            update(companyId, plan.getId(), planDto);
                        });
            }
        }

        CompanyPlan entity = CompanyPlanConverter.toEntityModel(resource, new CompanyPlan());
        entity.setCompany(companyEntity);
        entity.setPlan(lnfPlan);
        save(entity);
        log.debug("CompanyPlan for company {} successfully created", companyId);
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    @Cacheable(value = "companyPlan", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#planId)")
    private Plan searchForPlan(UUID planId) {
        return lnfPlanRepository.findById(planId).
                orElseThrow(() -> new LnFEntityNotFoundException("LnfPlan with id [%s] does not exist".formatted(planId)));
    }

    private void save(CompanyPlan entity) {
        try {
            CompanyPlan companyPlan = companyPlanRepository.save(entity);
            createCompanyPlanAudit(companyPlan);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyPlan for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyId, UUID companyPlanId, CompanyPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update CompanyPlan with null payload");
        searchForCompany(companyId);
        CompanyPlan entity = searchForCompanyPlan(companyPlanId);
        save(CompanyPlanConverter.toEntityModel(resource, entity));
        log.debug("CompanyPlan for Plan {} successfully created", companyPlanId);
    }

    private CompanyPlan searchForCompanyPlan(UUID companyPlanId) {
        return companyPlanRepository.findById(companyPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyPlan with id [%s] does not exist".formatted(companyPlanId)));
    }

    @CacheEvict(value = "companyPlan", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#companyPlanId)")
    public void deleteById(UUID companyId, UUID companyPlanId) {
        searchForCompany(companyId);
        CompanyPlan entity = searchForCompanyPlan(companyPlanId);
        try {
            companyPlanRepository.delete(entity);
            log.debug("CompanyPlan {} for company {} successfully deleted", companyPlanId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlan[%s] for company [%s]".formatted(companyPlanId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPlan> entities = companyPlanRepository.findByCompanyId(companyId);
        try {
            companyPlanRepository.deleteAll(entities);
            log.debug("CompanyPlan for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlan for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyPlanDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPlan> entities = companyPlanRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyPlanConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Cacheable(value = "companyPlan", key = "T(com.lnf.tenant.core.util.CacheKeyUtils).tenantAwareKey(#companyPlanId)")
    public CompanyPlanDto findById(UUID companyId, UUID companyPlanId) {
        searchForCompany(companyId);
        return CompanyPlanConverter.toTransportModel(searchForCompanyPlan(companyPlanId));
    }

    private Page<CompanyPlanDto> validateAndGetPages(int page, Page<CompanyPlan> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyPlanConverter::toTransportModel);
    }

    private void createCompanyPlanAudit(CompanyPlan plan) {
        CompanyPlanAuditDto planAuditDto = new CompanyPlanAuditDto();
        planAuditDto.setCompanyPlanId(plan.getId());
        planAuditDto.setStartDate(plan.getStartDate());
        planAuditDto.setEndDate(plan.getEndDate());
        planAuditDto.setStatus(plan.getStatus().name());
        lnfPlanAuditService.create(plan.getId(), planAuditDto);
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