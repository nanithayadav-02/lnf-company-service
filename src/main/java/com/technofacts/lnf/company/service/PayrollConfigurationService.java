package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.PayrollConfigurationConverter;
import com.technofacts.lnf.company.exception.LnFBadRequestException;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.exception.LnFException;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.PayrollConfiguration;
import com.technofacts.lnf.company.repository.CompanyRepository;
import com.technofacts.lnf.company.repository.PayrollConfigurationRepository;
import com.technofacts.lnf.dto.company.PayrollConfigurationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class PayrollConfigurationService {

    private final CompanyRepository companyRepository;

    private final PayrollConfigurationRepository payrollConfigurationRepository;

    public PayrollConfigurationDto findByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        PayrollConfiguration entity = payrollConfigurationRepository.findByCompanyId(companyId);
        PayrollConfigurationDto payrollConfigurationDto = PayrollConfigurationConverter.toTransportModel(entity);
        return payrollConfigurationDto;
    }

    public void create(UUID companyId, PayrollConfigurationDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create PayrollConfiguration for company [%s] with null payload", companyId));
        Company company = searchForCompany(companyId);
        PayrollConfiguration entity = PayrollConfigurationConverter.toEntityModel(resource, new PayrollConfiguration());
        entity.setCompany(company);
        save(entity);
        log.info(() -> String.format("PayrollConfiguration for Company[%s] successfully created", companyId));
    }

    public void update(UUID companyId, UUID payrollConfigurationId, PayrollConfigurationDto resource) {
        LnFBadRequestException.throwOnCondition(Objects::isNull, resource, String.format("Failed to create PayrollConfiguration for company [%s] with null payload", companyId));
        searchForCompany(companyId);
        PayrollConfiguration entity = searchForPayrollConfiguration(payrollConfigurationId);
        save(PayrollConfigurationConverter.toEntityModel(resource, entity));
        log.info(() -> String.format("PayrollConfiguration for Company[%s] successfully created", companyId));
    }

    private void save(PayrollConfiguration entity) {
        try {
            payrollConfigurationRepository.save(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to save PayrollConfiguration for company [%s]", entity.getCompany().getId());
            throw new LnFException(errorMessage);
        }
    }

    private Company searchForCompany(UUID companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> new com.technofacts.lnf.exception.LnFEntityNotFoundException(String.format("company with id [%s] does not exist", companyId)));
    }

    private PayrollConfiguration searchForPayrollConfiguration(UUID payrollConfigurationId) {
        return payrollConfigurationRepository.findById(payrollConfigurationId).orElseThrow(() -> new LnFEntityNotFoundException(String.format("payrollConfiguration with id [%s] does not exist", payrollConfigurationId)));
    }

    public void deleteByCompanyId(UUID companyId) {
        searchForCompany(companyId);
        PayrollConfiguration entity = payrollConfigurationRepository.findByCompanyId(companyId);
        try {
            payrollConfigurationRepository.delete(entity);
        } catch (RuntimeException e) {
            String errorMessage = String.format("Failed to delete payrollConfiguration for company [%s]", companyId);
            throw new com.technofacts.lnf.exception.LnFException(errorMessage, e);
        }
    }
}
