package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.GstConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyGst;
import com.technofacts.lnf.company.repository.CompanyGstRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.GstDto;
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
public class CompanyGstService {

    private final CompanyGstRepository repository;
    private final CompanyRepository companyRepository;

    public List<GstDto> findAll() {
        List<CompanyGst> entities = repository.findAll();
        return entities.stream().map(GstConverter::toTransportModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<GstDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyGst> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(GstConverter::toTransportModel).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public GstDto findById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        return GstConverter.toTransportModel(searchForGst(gstId));
    }

    public void create(UUID companyId, List<GstDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create gst for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        List<CompanyGst> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(gstDto -> {
            CompanyGst entity = GstConverter.toEntityModel(gstDto);
            entity.setCompany(companyEntity);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("Gst for company[%s] successfully created", companyId));
    }

    public void create(UUID companyId, GstDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, 
                String.format("Failed to create gst for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyGst entity = GstConverter.toEntityModel(resource);
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("Gst for company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID gstId, GstDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, 
                String.format("Failed to gst company[%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        searchForGst(gstId);
        CompanyGst updatedEntity = GstConverter.toEntityModel(resource);
        updatedEntity.setCompany(companyEntity);
        save(updatedEntity);
        log.info(() -> String.format("Gst for company[%s] successfully updated", companyId));
    }

    public void deleteById(UUID companyId, UUID gstId) {
        searchForCompany(companyId);
        CompanyGst entity = searchForGst(gstId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Gst[%s] for company[%s] successfully deleted", gstId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete gst[%s] for company [%s]", gstId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyGst> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.info(() -> String.format("Gsts for company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete gsts for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyGst entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save gst for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyGst> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save gst for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist", companyId)));
    }

    private CompanyGst searchForGst(UUID gstId) {
        return repository.findById(gstId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Gst with id [%s] does not exist", gstId)));
    }
}
