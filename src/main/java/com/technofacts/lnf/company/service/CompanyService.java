package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.specification.company.CompanySpecificationBuilder;
import com.technofacts.lnf.company.util.RestUtil;
import com.technofacts.lnf.dto.company.CompanyDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Transactional
@Service
@RequiredArgsConstructor
@Log
public class CompanyService {

    private static final String SEARCH_REGEX_PATTERN = "([\\w+?\\-_]+)(:|<|>)([\\w+?\\-_.@\\s]+),";

    private final CompanyRepository repository;

    public List<CompanyDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<Company> resultPage = repository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    public List<CompanyDto> findPaginated(int page, int size) {
        Page<Company> resultPage = repository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    public List<CompanyDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<Company> entities = Lists.newArrayList(repository.findAll(sortInfo));
        return entities.stream().map(CompanyConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<CompanyDto> findAll() {
        List<Company> entities = repository.findAll();
        return entities.stream().map(CompanyConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<CompanyDto> findAll(String search) {
        CompanySpecificationBuilder builder = new CompanySpecificationBuilder();
        Pattern pattern = Pattern.compile(SEARCH_REGEX_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(URLDecoder.decode(search, StandardCharsets.UTF_8) + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        Specification<Company> specification = builder.build();
        List<Company> entities = repository.findAll(specification);
        return entities.stream().map(CompanyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    private List<CompanyDto> validateAndGetPages(int page, Page<Company> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " +
                    "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        List<Company> entities = Lists.newArrayList(resultPage.getContent());
        return entities.stream().map(CompanyConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Company saveEntity(Company entity) {
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save company [%s]", entity.getCode());
            throw new LnFException(errorMessage, e);
        }
    }

    private Company search(UUID companyId) {
        return repository.findById(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private Company search(String companyCode) {
        return repository.findByCompanyCode(companyCode).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with code [%s] does not exist", companyCode)));
    }
}

