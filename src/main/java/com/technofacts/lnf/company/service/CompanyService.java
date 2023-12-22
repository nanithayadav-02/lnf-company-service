package com.technofacts.lnf.company.service;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import com.technofacts.lnf.dto.recruiter.ApplicantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyService {

    private static final String SEARCH_REGEX_PATTERN = "([\\w]+)\\s*:\\s*([\\w.@\\- ]+?)(?=(,|$))";

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
        final CompanySpecificationBuilder builder = new CompanySpecificationBuilder();
        createSearchConditions(search, builder);
        return builder.build();
    }

    private void createSearchConditions(String search, CompanySpecificationBuilder builder) {
        log.info(() -> String.format("search [%s]", search));
        log.info(() -> String.format("builder [%s]", builder.toString()));
        final Matcher matcher = Pattern.compile(SEARCH_REGEX_PATTERN, Pattern.CASE_INSENSITIVE).matcher(search);
        while (matcher.find()) {
            processSearchGroup(matcher, builder);
        }
    }

    private void processSearchGroup(final Matcher matcher, final CompanySpecificationBuilder builder) {
        String key = matcher.group(1).trim();
        String value = matcher.group(2).trim();
        log.info("Key: " + key + ", Value: " + value);
        Class<?> fieldType = getFieldClass(key);
        addCondition(builder, key, fieldType, value);
    }

    private void addCondition(final CompanySpecificationBuilder builder, final String key, Class<?> fieldType, final String value) {
        if (fieldType != null) {
            Object convertedValue = convertToFieldType(fieldType, value);
            builder.with(key, ":", convertedValue);
        }
    }
    private Class<?> getFieldClass(String fieldName) {
        try {
            Class<?> clazz = Class.forName("com.technofacts.lnf.company.model.Company");
            Field field = clazz.getDeclaredField(fieldName);
            return field.getType();
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            return null;
        }
    }

    private Object convertToFieldType(Class<?> fieldType, String value) {
        if (fieldType.isEnum()) {
            return getEnumConstant(fieldType, value);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.valueOf(value);
        } else {
            return value;
        }
    }

    private Enum<?> getEnumConstant(Class<?> fieldType, String value) {
        String uppercaseValue = value.toUpperCase();
        for (Enum<?> enumConstant : ((Class<? extends Enum>) fieldType).getEnumConstants()) {
            if (enumConstant.name().toUpperCase().equals(uppercaseValue)) {
                return enumConstant;
            }
        }
        return null;
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

