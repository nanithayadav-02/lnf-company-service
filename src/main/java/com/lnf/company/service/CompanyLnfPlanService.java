package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyLnfPlanConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.Company;
import com.lnf.company.model.CompanyLnfPlan;
import com.lnf.company.model.LnfPlan;
import com.lnf.company.repository.CompanyLnfPlanRepository;
import com.lnf.company.repository.CompanyRepository;
import com.lnf.company.repository.LnfPlanRepository;
import com.lnf.dto.company.CompanyLnfPlanAuditDto;
import com.lnf.dto.company.CompanyLnfPlanDto;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyLnfPlanService implements PaginatedAndSortedService<CompanyLnfPlanDto> {

    private final CompanyRepository companyRepository;
    private final CompanyLnfPlanRepository companyLnfPlanRepository;
    private final LnfPlanRepository lnfPlanRepository;
    private final CompanyLnfPlanAuditService lnfPlanAuditService;

    @Override
    public Page<CompanyLnfPlanDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyLnfPlan> resultPage = companyLnfPlanRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyLnfPlanDto> findPaginated(int page, int size) {
        Page<CompanyLnfPlan> resultPage = companyLnfPlanRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyLnfPlanDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyLnfPlan> entities = Lists.newArrayList(companyLnfPlanRepository.findAll(sortInfo));
        return entities.stream().map(CompanyLnfPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<CompanyLnfPlanDto> findAll() {
        return companyLnfPlanRepository.findAll().stream().
                map(CompanyLnfPlanConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyId, CompanyLnfPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyLnfPlan for company [%s] with null payload".formatted(companyId));
        Company companyEntity = searchForCompany(companyId);
        LnfPlan lnfPlan = searchForLnfPlan(resource.getLnfPlanId());
        CompanyLnfPlan entity = CompanyLnfPlanConverter.toEntityModel(resource, new CompanyLnfPlan());
        entity.setCompany(companyEntity);
        entity.setLnfPlan(lnfPlan);
        save(entity);
        log.debug("CompanyLnfPlan for company {} successfully created", companyId);
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException("Company with id [%s] does not exist".formatted(companyId)));
    }

    private LnfPlan searchForLnfPlan(UUID lnfPlanId) {
        return lnfPlanRepository.findById(lnfPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("LnfPlan with id [%s] does not exist".formatted(lnfPlanId)));
    }

    private void save(CompanyLnfPlan entity) {
        try {
            CompanyLnfPlan companyLnfPlan = companyLnfPlanRepository.save(entity);
            createCompanyLnfPlanAudit(companyLnfPlan);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyLnfPlan for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyId, UUID companyLnfPlanId, CompanyLnfPlanDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update CompanyLnfPlan with null payload");
        searchForCompany(companyId);
        CompanyLnfPlan entity = searchForCompanyLnfPlan(companyLnfPlanId);
        save(CompanyLnfPlanConverter.toEntityModel(resource, entity));
        log.debug("CompanyLnfPlan for Employee {} successfully created", companyLnfPlanId);
    }

    private CompanyLnfPlan searchForCompanyLnfPlan(UUID companyLnfPlanId) {
        return companyLnfPlanRepository.findById(companyLnfPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyLnfPlan with id [%s] does not exist".formatted(companyLnfPlanId)));
    }

    public void deleteById(UUID companyId, UUID companyLnfPlanId) {
        searchForCompany(companyId);
        CompanyLnfPlan entity = searchForCompanyLnfPlan(companyLnfPlanId);
        try {
            companyLnfPlanRepository.delete(entity);
            log.debug("CompanyLnfPlan {} for company {} successfully deleted", companyLnfPlanId, companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyLnfPlan[%s] for company [%s]".formatted(companyLnfPlanId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyLnfPlan> entities = companyLnfPlanRepository.findByCompanyId(companyId);
        try {
            companyLnfPlanRepository.deleteAll(entities);
            log.debug("CompanyLnfPlan for company {} successfully deleted", companyId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyLnfPlan for company [%s]".formatted(companyId);
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyLnfPlanDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyLnfPlan> entities = companyLnfPlanRepository.findByCompanyId(companyId);
        return entities.stream().map(CompanyLnfPlanConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public CompanyLnfPlanDto findById(UUID companyId, UUID CompanyLnfPlanId) {
        searchForCompany(companyId);
        return CompanyLnfPlanConverter.toTransportModel(searchForCompanyLnfPlan(CompanyLnfPlanId));
    }

    private Page<CompanyLnfPlanDto> validateAndGetPages(int page, Page<CompanyLnfPlan> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyLnfPlanConverter::toTransportModel);
    }

    private void createCompanyLnfPlanAudit(CompanyLnfPlan plan) {
        CompanyLnfPlanAuditDto planAuditDto = new CompanyLnfPlanAuditDto();
        planAuditDto.setCompanyPlanId(plan.getId());
        planAuditDto.setStartDate(plan.getStartDate());
        planAuditDto.setStatus(plan.getStatus().name());
        lnfPlanAuditService.create(plan.getId(), planAuditDto);
    }

}