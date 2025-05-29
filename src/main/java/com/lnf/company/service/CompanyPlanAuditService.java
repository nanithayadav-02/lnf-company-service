package com.lnf.company.service;

import com.google.common.collect.Lists;
import com.lnf.company.converter.CompanyPlanAuditConverter;
import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.CompanyPlanAudit;
import com.lnf.company.repository.CompanyPlanAuditRepository;
import com.lnf.company.repository.CompanyPlanRepository;
import com.lnf.dto.company.CompanyPlanAuditDto;
import com.lnf.exception.LnFBadRequestException;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.exception.LnFException;
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
public class CompanyPlanAuditService implements PaginatedAndSortedService<CompanyPlanAuditDto> {

    private final CompanyPlanRepository companyPlanRepository;
    private final CompanyPlanAuditRepository repository;

    @Override
    public Page<CompanyPlanAuditDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyPlanAudit> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyPlanAuditDto> findPaginated(int page, int size) {
        Page<CompanyPlanAudit> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyPlanAuditDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyPlanAudit> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().filter(Objects::nonNull).map(CompanyPlanAuditConverter::toTransportModel)
                .toList();
    }

    @Override
    public List<CompanyPlanAuditDto> findAll() {
        return repository.findAll().stream().
                map(CompanyPlanAuditConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    public void create(UUID companyPlanId, List<CompanyPlanAuditDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyPlanAudit for company [%s] with null payload".formatted(companyPlanId));
        CompanyPlan companyEntity = searchForCompanyPlan(companyPlanId);
        List<CompanyPlanAudit> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(companyPlanAuditDto -> {
            CompanyPlanAudit entity = CompanyPlanAuditConverter.toEntityModel(companyPlanAuditDto, new CompanyPlanAudit());
            entity.setCompanyPlan(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.debug("CompanyPlanAudit for company {} successfully created", companyPlanId);
    }

    public void create(UUID companyPlanId, CompanyPlanAuditDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create CompanyPlanAudit for company [%s] with null payload".formatted(companyPlanId));
        CompanyPlan companyEntity = searchForCompanyPlan(companyPlanId);
        CompanyPlanAudit entity = CompanyPlanAuditConverter.toEntityModel(resource, new CompanyPlanAudit());
        entity.setCompanyPlan(companyEntity);
        save(entity);
        log.debug("CompanyPlanAudit for company {} successfully created", companyPlanId);
    }

    private CompanyPlan searchForCompanyPlan(UUID companyPlanId) {
        return companyPlanRepository.findById(companyPlanId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyPlan with id [%s] does not exist".formatted(companyPlanId)));
    }

    private void save(List<CompanyPlanAudit> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyPlanAudit for company [%s]", entities.getFirst().getCompanyPlan().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyPlanAudit entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save CompanyPlanAudit for CompanyPlan [%s]", entity.getCompanyPlan().getId());
            throw new LnFException(errorMessage);
        }
    }

    public void update(UUID companyPlanId, UUID companyPlanAuditId, CompanyPlanAuditDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, "Failed to update CompanyPlanAudit with null payload");
        searchForCompanyPlan(companyPlanId);
        CompanyPlanAudit entity = searchForCompanyPlanAudit(companyPlanAuditId);
        save(CompanyPlanAuditConverter.toEntityModel(resource, entity));
        log.debug("CompanyPlanAudit for Employee {} successfully created", companyPlanAuditId);
    }

    private CompanyPlanAudit searchForCompanyPlanAudit(UUID companyPlanAuditId) {
        return repository.findById(companyPlanAuditId).
                orElseThrow(() -> new LnFEntityNotFoundException("CompanyPlanAudit with id [%s] does not exist".formatted(companyPlanAuditId)));
    }

    public void deleteById(UUID companyPlanId, UUID companyPlanAuditId) {
        searchForCompanyPlan(companyPlanId);
        CompanyPlanAudit entity = searchForCompanyPlanAudit(companyPlanAuditId);
        try {
            repository.delete(entity);
            log.debug("CompanyPlanAudit {} for company {} successfully deleted", companyPlanAuditId, companyPlanId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlanAudit[%s] for company [%s]".formatted(companyPlanAuditId, companyPlanId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyPlanId(UUID companyPlanId) {
        searchForCompanyPlan(companyPlanId);
        List<CompanyPlanAudit> entities = repository.findByCompanyPlanId(companyPlanId);
        try {
            repository.deleteAll(entities);
            log.debug("CompanyPlanAudit for company {} successfully deleted", companyPlanId);
        } catch (RuntimeException e) {
            String errorMessage = "Failed to delete CompanyPlanAudit for company [%s]".formatted(companyPlanId);
            throw new LnFException(errorMessage);
        }
    }

    public List<CompanyPlanAuditDto> findByCompanyPlanId(UUID companyPlanId) {
        searchForCompanyPlan(companyPlanId);
        List<CompanyPlanAudit> entities = repository.findByCompanyPlanId(companyPlanId);
        return entities.stream().filter(Objects::nonNull).map(CompanyPlanAuditConverter::toTransportModel)
                .toList();
    }

    public CompanyPlanAuditDto findById(UUID companyPlanId, UUID companyPlanAuditId) {
        searchForCompanyPlan(companyPlanId);
        return CompanyPlanAuditConverter.toTransportModel(searchForCompanyPlanAudit(companyPlanAuditId));
    }

    private Page<CompanyPlanAuditDto> validateAndGetPages(int page, Page<CompanyPlanAudit> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(("Total number of pages [%d], " +
                    "requested page [%d] does not exist").formatted(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyPlanAuditConverter::toTransportModel);
    }

    public List<CompanyPlanAuditDto> findByCompanyId(UUID companyId) {
        List<CompanyPlanAudit> entities = repository.findByCompanyId(companyId);
        return entities.stream().filter(Objects::nonNull).map(CompanyPlanAuditConverter::toTransportModel)
                .toList();
    }

}