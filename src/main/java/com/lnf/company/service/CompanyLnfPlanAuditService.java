package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyLnfPlanAuditConverter;
import com.lnf.company.exception.LnFBadRequestException;
import com.lnf.company.exception.LnFEntityNotFoundException;
import com.lnf.company.exception.LnFException;
import com.lnf.company.model.CompanyLnfPlan;
import com.lnf.company.model.CompanyLnfPlanAudit;
import com.lnf.company.repository.CompanyLnfPlanAuditRepository;
import com.lnf.company.repository.CompanyLnfPlanRepository;
import com.lnf.dto.company.CompanyLnfPlanAuditDto;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyLnfPlanAuditService implements PaginatedAndSortedService<CompanyLnfPlanAuditDto> {

    private final CompanyLnfPlanRepository companyLnfPlanRepository;
    private final CompanyLnfPlanAuditRepository repository;

    @Override
    public Page<CompanyLnfPlanAuditDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyLnfPlanAudit> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyLnfPlanAuditDto> findPaginated(int page, int size) {
        Page<CompanyLnfPlanAudit> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyLnfPlanAuditDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyLnfPlanAudit> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(CompanyLnfPlanAuditConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<CompanyLnfPlanAuditDto> findAll() {
        return repository.findAll().stream().
                map(CompanyLnfPlanAuditConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyLnfPlanId, List<CompanyLnfPlanAuditDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyLnfPlanAudit for company [%s] with null payload".formatted(companyLnfPlanId));
        CompanyLnfPlan companyEntity = searchForCompanyLnfPlan(companyLnfPlanId);
        List<CompanyLnfPlanAudit> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(CompanyLnfPlanAuditDto -> {
            CompanyLnfPlanAudit entity = CompanyLnfPlanAuditConverter.toEntityModel(CompanyLnfPlanAuditDto, new CompanyLnfPlanAudit());
            entity.setCompanyLnfPlan(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.debug("CompanyLnfPlanAudit for company {} successfully created", companyLnfPlanId);
    }

    public void create(UUID companyLnfPlanId, CompanyLnfPlanAuditDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyLnfPlanAudit for company [%s] with null payload".formatted(companyLnfPlanId));
        CompanyLnfPlan companyEntity = searchForCompanyLnfPlan(companyLnfPlanId);
        CompanyLnfPlanAudit entity = CompanyLnfPlanAuditConverter.toEntityModel(resource, new CompanyLnfPlanAudit());
        entity.setCompanyLnfPlan(companyEntity);
        save(entity);
        log.debug("CompanyLnfPlanAudit for company {} successfully created", companyLnfPlanId);
    }

    private CompanyLnfPlan searchForCompanyLnfPlan(UUID companyLnfPlanId) {
        return companyLnfPlanRepository.findById(companyLnfPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyLnfPlan with id [%s] does not exist".formatted(companyLnfPlanId)));
    }

    private void save(List<CompanyLnfPlanAudit> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyLnfPlanAudit for company [%s]", entities.getFirst().getCompanyLnfPlan().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyLnfPlanAudit entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyLnfPlanAudit for companyLnfPlan [%s]", entity.getCompanyLnfPlan().getId());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyLnfPlanId, UUID CompanyLnfPlanAuditId, CompanyLnfPlanAuditDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update CompanyLnfPlanAudit with null payload");
        searchForCompanyLnfPlan(companyLnfPlanId);
        CompanyLnfPlanAudit entity = searchForCompanyLnfPlanAudit(CompanyLnfPlanAuditId);
        save(CompanyLnfPlanAuditConverter.toEntityModel(resource, entity));
        log.debug("CompanyLnfPlanAudit for Employee {} successfully created", CompanyLnfPlanAuditId);
    }

    private CompanyLnfPlanAudit searchForCompanyLnfPlanAudit(UUID CompanyLnfPlanAuditId) {
        return repository.findById(CompanyLnfPlanAuditId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyLnfPlanAudit with id [%s] does not exist".formatted(CompanyLnfPlanAuditId)));
    }

    public void deleteById(UUID companyLnfPlanId, UUID CompanyLnfPlanAuditId) {
        searchForCompanyLnfPlan(companyLnfPlanId);
        CompanyLnfPlanAudit entity = searchForCompanyLnfPlanAudit(CompanyLnfPlanAuditId);
        try {
            repository.delete(entity);
            log.debug("CompanyLnfPlanAudit {} for company {} successfully deleted", CompanyLnfPlanAuditId, companyLnfPlanId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyLnfPlanAudit[%s] for company [%s]".formatted(CompanyLnfPlanAuditId, companyLnfPlanId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyLnfPlanId(UUID companyLnfPlanId) {
        searchForCompanyLnfPlan(companyLnfPlanId);
        List<CompanyLnfPlanAudit> entities = repository.findByCompanyLnfPlanId(companyLnfPlanId);
        try {
            repository.deleteAll(entities);
            log.debug("CompanyLnfPlanAudit for company {} successfully deleted", companyLnfPlanId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyLnfPlanAudit for company [%s]".formatted(companyLnfPlanId);
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyLnfPlanAuditDto> findByCompanyLnfPlanId(UUID companyLnfPlanId) {
        searchForCompanyLnfPlan(companyLnfPlanId);
        List<CompanyLnfPlanAudit> entities = repository.findByCompanyLnfPlanId(companyLnfPlanId);
        return entities.stream().map(CompanyLnfPlanAuditConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public CompanyLnfPlanAuditDto findById(UUID companyLnfPlanId, UUID CompanyLnfPlanAuditId) {
        searchForCompanyLnfPlan(companyLnfPlanId);
        return CompanyLnfPlanAuditConverter.toTransportModel(searchForCompanyLnfPlanAudit(CompanyLnfPlanAuditId));
    }

    private Page<CompanyLnfPlanAuditDto> validateAndGetPages(int page, Page<CompanyLnfPlanAudit> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyLnfPlanAuditConverter::toTransportModel);
    }

}