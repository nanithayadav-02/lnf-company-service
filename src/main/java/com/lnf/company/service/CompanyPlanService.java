package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyPlanConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.Plan;
import com.lnf.company.repository.CompanyPlanRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.company.repository.PlanRepository;
import com.lnf.dto.company.CompanyPlanAuditDto;
import com.lnf.dto.company.CompanyPlanDto;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyPlanService implements PaginatedAndSortedService<CompanyPlanDto> {

    private final CompanyRepository companyRepository;
    private final CompanyPlanRepository CompanyPlanRepository;
    private final PlanRepository lnfPlanRepository;
    private final CompanyPlanAuditService lnfPlanAuditService;
    private final CacheManager cacheManager;

    @Override
    public Page<CompanyPlanDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyPlan> resultPage = CompanyPlanRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyPlanDto> findPaginated(int page, int size) {
        Page<CompanyPlan> resultPage = CompanyPlanRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyPlanDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyPlan> entities = Lists.newArrayList(CompanyPlanRepository.findAll(sortInfo));
        return entities.stream().map(CompanyPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<CompanyPlanDto> findAll() {
        return CompanyPlanRepository.findAll().stream().
                map(CompanyPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyId, CompanyPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyPlan for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        Plan lnfPlan = searchForPlan(resource.getLnfPlanId());
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

    @Cacheable(value="companyPlan" ,key = "#planId")
    private Plan searchForPlan(UUID planId) {
        return lnfPlanRepository.findById(planId).
                orElseThrow(() -> new LnFEntityNotFoundException("LnfPlan with id [%s] does not exist".formatted(planId)));
    }

    private void save(CompanyPlan entity) {
        try {
            CompanyPlan CompanyPlan = CompanyPlanRepository.save(entity);
            createCompanyPlanAudit(CompanyPlan);
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
        log.debug("CompanyPlan for Employee {} successfully created", companyPlanId);
    }

    private CompanyPlan searchForCompanyPlan(UUID companyPlanId) {
        return CompanyPlanRepository.findById(companyPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyPlan with id [%s] does not exist".formatted(companyPlanId)));
    }

    @CacheEvict(value="companyPlan" ,key = "#companyPlanId")
    public void deleteById(UUID companyId, UUID companyPlanId) {
        searchForCompany(companyId);
        CompanyPlan entity = searchForCompanyPlan(companyPlanId);
        try {
            CompanyPlanRepository.delete(entity);
            log.debug("CompanyPlan {} for company {} successfully deleted", companyPlanId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlan[%s] for company [%s]".formatted(companyPlanId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPlan> entities = CompanyPlanRepository.findByCompanyId(companyId);
        try {
            CompanyPlanRepository.deleteAll(entities);
            log.debug("CompanyPlan for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlan for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

    @Cacheable(value="companyPlan" ,key = "#companyId")
    public List<CompanyPlanDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyPlan> entities = CompanyPlanRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyPlanConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Cacheable(value="companyPlan" ,key = "#companyPlanId")
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
        planAuditDto.setStatus(plan.getStatus().name());
        lnfPlanAuditService.create(plan.getId(), planAuditDto);
    }

    public void clearCaches() {
        Objects.requireNonNull(cacheManager.getCache("companyPlan")).clear();
        log.debug("CompanyPlan cache cleared.");
    }

}