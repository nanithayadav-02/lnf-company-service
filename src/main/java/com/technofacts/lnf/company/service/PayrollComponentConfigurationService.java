package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.PayrollComponentConfigurationConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.PayrollComponentConfiguration;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.PayrollComponentConfigurationRepository;
import com.technofacts.lnf.dto.company.PayrollComponentConfigurationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
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
@Log
public class PayrollComponentConfigurationService {

    private final CompanyRepository companyRepository;

    private final PayrollComponentConfigurationRepository payrollComponentConfigurationRepository;


    public List<PayrollComponentConfigurationDto> findAll() {
        List<PayrollComponentConfiguration> entities = payrollComponentConfigurationRepository.findAll();
        return entities.stream().map(PayrollComponentConfigurationConverter::toTransportModel).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<PayrollComponentConfigurationDto> findByCompanyId(UUID companyId) {
        List<PayrollComponentConfiguration> entities = payrollComponentConfigurationRepository.findByCompanyId(companyId);
        return entities.stream().map(entity -> PayrollComponentConfigurationConverter.toTransportModel(entity)).collect(Collectors.toList());
    }


    public void create(UUID companyId, PayrollComponentConfigurationDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create PayrollComponentConfiguration for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        PayrollComponentConfiguration entity = PayrollComponentConfigurationConverter.toEntityModel(resource, new PayrollComponentConfiguration());
        entity.setCompany(company);
        save(entity);
        log.info(() -> String.format("PayrollComponentConfiguration for Company[%s] successfully created", companyId));
    }

    public void create(UUID companyId, List<PayrollComponentConfigurationDto> resources) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resources, String.format("Failed to create PayrollComponentConfiguration for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        List<PayrollComponentConfiguration> entities = new ArrayList<>();
        resources.stream().filter(Objects::nonNull).forEach(PayrollComponentConfigurationDto -> {
            PayrollComponentConfiguration entity = PayrollComponentConfigurationConverter.toEntityModel(PayrollComponentConfigurationDto, new PayrollComponentConfiguration());
            entity.setCompany(company);
            entities.add(entity);
        });
        save(entities);
        log.info(() -> String.format("PayrollComponentConfiguration for Company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID id, PayrollComponentConfigurationDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create PayrollComponentConfiguration for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        PayrollComponentConfiguration entity = searchForPayrollComponentConfiguration(id);
        save(PayrollComponentConfigurationConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("PayrollComponentConfiguration for Company[%s] successfully created", companyId));
    }

    private void save(PayrollComponentConfiguration entity) {
        try {
            payrollComponentConfigurationRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save PayrollComponentConfiguration for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private void save(List<PayrollComponentConfiguration> entities) {
        try {
            payrollComponentConfigurationRepository.saveAll(entities);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save PayrollComponentConfiguration for company [%s]", entities.get(0).getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }
    private Company searchForCompany(UUID companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> new com.technofacts.lnf.exception.LnFEntityNotFoundException(String.format("company with id [%s] does not exist", companyId)));
    }

    private PayrollComponentConfiguration searchForPayrollComponentConfiguration(UUID id) {
        return payrollComponentConfigurationRepository.findById(id).orElseThrow(() -> new LnFEntityNotFoundException(String.format("payrollComponentConfiguration with id [%s] does not exist", id)));
    }

    public void deleteByCompanyIdAndId(UUID companyId,UUID id) {
        searchForCompany(companyId);
        PayrollComponentConfiguration entity = searchForPayrollComponentConfiguration(id);
        try {
            payrollComponentConfigurationRepository.delete(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete PayrollComponentConfiguration for company [%s]", companyId);
            throw new com.technofacts.lnf.exception.LnFException(errorMessage, e);
        }
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        List<PayrollComponentConfiguration> entities = payrollComponentConfigurationRepository.findByCompanyId(companyId);
        try {
            payrollComponentConfigurationRepository.deleteAll(entities);
            log.info(() -> String.format("PayrollComponentConfiguration for company[%s] successfully deleted", companyId));
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete PayrollComponentConfiguration for company [%s]", companyId);
            throw new LnFException(errorMessage);
        }
    }
}
