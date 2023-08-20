package com.technofacts.lnf.company.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.technofacts.lnf.company.converter.AddressConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyAddress;
import com.technofacts.lnf.company.repository.CompanyAddressRepository;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.dto.company.AddressDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyAddressService {

    private final CompanyAddressRepository repository;
    private final CompanyRepository companyRepository;

    public List<AddressDto> findAll() {
        List<CompanyAddress> entities = repository.findAll();
        return entities.stream().map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<AddressDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyAddress> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public AddressDto findById(UUID companyId, UUID addressId) {
        searchForCompany(companyId);
        return AddressConverter.toTransportModel(searchForAddress(addressId));
    }

    public void create(UUID companyId, List<AddressDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create Address for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        List<CompanyAddress> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(addressDto -> {
            CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(addressDto, new CompanyAddress());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("Address for Company[%s] successfully created", companyId));
    }

    public void create(UUID companyId, AddressDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create Address for company [%s] with null payload", companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(resource, new CompanyAddress());
        entity.setCompany(companyEntity);
        save(entity);
        log.info(() -> String.format("Address for Company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID addressId, AddressDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format("Failed to create Address for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        CompanyAddress entity = searchForAddress(addressId);
        save((CompanyAddress) AddressConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("Address for Company[%s] successfully created", companyId));
    }

    public void deleteById(UUID companyId, UUID addressId) {
        searchForCompany(companyId);
        CompanyAddress entity = searchForAddress(addressId);
        try {
            repository.delete(entity);
            log.info(() -> String.format("Address[%s] for company [%s] successfully deleted", addressId, companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Address[[%s] for company [%s]", addressId, companyId);
            throw new LnFException(errorMessage);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyAddress> entities = repository.findByCompanyId(companyId);
        try {
            repository.deleteAll(entities);
            log.info(() -> String.format("Address for Company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete Address for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }

    private void save(CompanyAddress entity) {
        try {
            repository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Address for company [%s]",
                    entity.getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<CompanyAddress> entities) {
        try {
            repository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save Address for company [%s]",
                    entities.get(0).getCompany().getCode());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findByCompanyId(companyId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Company with id [%s] does not exist",
                        companyId)));
    }

    private CompanyAddress searchForAddress(UUID addressId) {
        return repository.findById(addressId).
                orElseThrow(() -> new LnFEntityNotFoundException(String.format("Address with id [%s] does not exist",
                        addressId)));
    }

}
