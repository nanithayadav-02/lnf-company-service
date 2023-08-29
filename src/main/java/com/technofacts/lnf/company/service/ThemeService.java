package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.ThemeConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.Theme;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.ThemeRepository;
import com.technofacts.lnf.dto.company.ThemeDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class ThemeService {

    private final ThemeRepository repository;
    private final CompanyRepository companyRepository;

    public List<ThemeDto> findAll() {
        List<Theme> entities = repository.findAll();
        return entities.stream().map(ThemeConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ThemeDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Theme> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(ThemeConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ThemeDto findById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        return ThemeConverter.toTransportModel(searchForGst(gstId));
    }

    public void create(UUID companyId, List<ThemeDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create theme for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        List<Theme> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(gstDto -> {
            Theme entity = ThemeConverter.toEntityModel(gstDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("Theme for company[%s] successfully created", companyId));
    }

    public void create(UUID companyId, ThemeDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, 
                String.format("Failed to create theme for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        Theme entity = ThemeConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("Theme for company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID gstId, ThemeDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, 
                String.format("Failed to theme company[%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        searchForGst(gstId);
        Theme updatedEntity = ThemeConverter.toEntityModel(resource);
        updatedEntity.setCompany(companyEntity);
        save(updatedEntity);
        log.info(() -> String.format("Theme for company[%s] successfully updated", companyId));
    }

    public void deleteById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        Theme entity = searchForGst(gstId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Theme[%s] for company[%s] successfully deleted", gstId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete theme[%s] for company [%s]", gstId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<Theme> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.info(() -> String.format("Theme for company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete gsts for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(Theme entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save theme for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<Theme> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save theme for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private Theme searchForGst(UUID gstId) {
        return repository.findById(gstId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Theme with id [%s] does not exist", gstId)));
    }

}
