package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.CompanyDto;
import com.technofacts.lnf.service.common.page.PaginatedAndSortedService;
import com.technofacts.lnf.service.specification.GenericSpecificationBuilder;
import com.technofacts.lnf.util.RestUtil;
import com.technofacts.lnf.util.specification.SpecificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyService implements PaginatedAndSortedService<CompanyDto> {

    private final CompanyRepository repository;

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
                .map(CompanyConverter::toTransportModel)
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

    public CompanyDto findByCompanyCode(String companyCode) {
        Company entity = search(companyCode);
        return CompanyConverter.toTransportModel(entity);
    }

    public void create(CompanyDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to create Company with null payload");
        Company entity = CompanyConverter.toEntityModel(resource);
        saveEntity(entity);
        log.info(() -> String.format("Company[%s] successfully created", entity.getCode()));
    }

    @Transactional
    public void update(UUID companyId, CompanyDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                "Failed to update Company with null payload");
        Company entity = search(companyId);
        Company updatedEntity = CompanyConverter.toEntityModel(resource);
        updatedEntity.setId(entity.getId());
        saveEntity(updatedEntity);
        log.info(() -> String.format("Company[%s] successfully updated", companyId));
    }

    public void delete(UUID companyId) {
        Company entity = search(companyId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Company[%s] successfully deleted", entity.getCode()));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Company [%s]", entity.getCode());
            throw new LnFException(errorMessage, e);
        }
    }

    private Page<CompanyDto> validateAndGetPages(int page, Page<Company> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " +
                    "requested page [%d] does not exist", resultPage.getTotalPages(), page));
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
        return new LnFEntityNotFoundException(String.format("Company with id/code [%s] does not exist", companyIdentifier));
    }

}
