package com.technofacts.lnf.company.service;

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
import lombok.extern.slf4j.Slf4j;
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
public class CompanyAddressService {

    public static final String ADDRESS_FOR_COMPANY_SUCCESSFULLY_CREATED = "Address for Company {} successfully created";
    public static final String FAILED_TO_CREATE_ADDRESS_FOR_COMPANY_S_WITH_NULL_PAYLOAD = "Failed to create Address for company [%s] with null payload";
    private final CompanyAddressRepository repository;
    private final CompanyRepository companyRepository;

    public List<AddressDto> findAll() {
        List<CompanyAddress> entities = repository.findAll();
        return entities.stream().map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).toList();
    }

    public List<AddressDto> findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<CompanyAddress> entities = repository.findByCompanyId(companyId);
        return entities.stream().map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).toList();
    }

    public AddressDto findById(UUID companyId, UUID addressId) {
        searchForCompany(companyId);
        return AddressConverter.toTransportModel(searchForAddress(addressId));
    }

    public void create(UUID companyId, List<AddressDto> resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format(FAILED_TO_CREATE_ADDRESS_FOR_COMPANY_S_WITH_NULL_PAYLOAD, companyId));
        Company company = searchForCompany(companyId);
        List<CompanyAddress> entities = new ArrayList<>();
        resource.stream().filter(Objects::nonNull).forEach(addressDto -> {
            CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(addressDto, new CompanyAddress());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.debug(ADDRESS_FOR_COMPANY_SUCCESSFULLY_CREATED, companyId);
    }

    public void create(UUID companyId, AddressDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format(FAILED_TO_CREATE_ADDRESS_FOR_COMPANY_S_WITH_NULL_PAYLOAD, companyId));
        Company companyEntity = searchForCompany(companyId);
        CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(resource, new CompanyAddress());
        entity.setCompany(companyEntity);
        save(entity);
        log.debug(ADDRESS_FOR_COMPANY_SUCCESSFULLY_CREATED, companyId);
    }

    public void update(UUID companyId, UUID addressId, AddressDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource,
                String.format(FAILED_TO_CREATE_ADDRESS_FOR_COMPANY_S_WITH_NULL_PAYLOAD, companyId));
        searchForCompany(companyId);
        CompanyAddress entity = searchForAddress(addressId);
        save((CompanyAddress) AddressConverter.toEntityModel(resource, entity));
        log.debug(ADDRESS_FOR_COMPANY_SUCCESSFULLY_CREATED, companyId);
    }

    public void deleteById(UUID companyId, UUID addressId) {
        searchForCompany(companyId);
        CompanyAddress entity = searchForAddress(addressId);
        try {
            repository.delete(entity);
            log.debug("Address {} for company {} successfully deleted", addressId, companyId);
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
            log.debug("Address for Company {} successfully deleted", companyId);
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
