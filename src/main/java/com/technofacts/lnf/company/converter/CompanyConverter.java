package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.*;
import com.technofacts.lnf.dto.company.CompanyDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompanyConverter {

    public static CompanyDto toTransportModel(Company entity) {

        if (entity == null) {
            return null;
        }
        CompanyDto dto = CompanyDto.builder().id(entity.getId()).code(entity.getCode()).name(entity.getName()).status(entity.getStatus()).email(entity.getEmail()).telephone(entity.getTelephone()).mobile(entity.getMobile()).website(entity.getWebsite()).businessCategory(entity.getBusinessCategory()).businessDescription(entity.getBusinessDescription()).pan(entity.getPan()).arn(entity.getArn()).arnIssueDate(entity.getArnIssueDate()).sacCode(entity.getSacCode()).address(new ArrayList<>()).gst(new ArrayList<>()).account(new ArrayList<>()).theme(new ArrayList<>()).notes(new ArrayList<>()).companyEvent(new ArrayList<>()).companyHoliday(new ArrayList<>()).build();

        dto.getAddress().addAll(entity.getAddress().stream().map(AddressConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getGst().addAll(entity.getGst().stream().map(GstConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getAccount().addAll(entity.getAccount().stream().map(AccountConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getTheme().addAll(entity.getTheme().stream().map(ThemeConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getNotes().addAll(entity.getCompanyNotes().stream().map(CompanyNotesConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getCompanyEvent().addAll(entity.getCompanyEvent().stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getPayrollComponentConfiguration().addAll(entity.getPayrollComponentConfiguration().stream().map(PayrollComponentConfigurationConverter::toTransportModel).filter(Objects::nonNull).toList());
        dto.getCompanyHoliday().addAll(entity.getCompanyHoliday().stream().map(CompanyHolidayConverter::toTransportModel).filter(Objects::isNull).toList());
        return dto;
    }

    public static Company toEntityModel(CompanyDto transport) {
        Company entity = toEntityModel(transport, new Company());

        addAddressToEntityModel(transport, entity);
        addGstToEntityModel(transport, entity);
        addAccountToEntityModel(transport, entity);
        addThemeToEntityModel(transport, entity);
        addNotesToEntityModel(transport, entity);
        addCompanyEventToEntityModel(transport, entity);
        addPayrollComponentConfigurationToEntityModel(transport,entity);
        addCompanyHolidayToEntityModel(transport, entity);
        return entity;
    }

    private static Company toEntityModel(CompanyDto transport, Company entity) {
        if (transport == null || entity == null) {
            return null;
        }

        entity.setId(transport.getId());
        entity.setCode(transport.getCode());
        entity.setName(transport.getName());
        entity.setStatus(transport.getStatus());
        entity.setEmail(transport.getEmail());
        entity.setTelephone(transport.getTelephone());
        entity.setMobile(transport.getMobile());
        entity.setWebsite(transport.getWebsite());
        entity.setBusinessCategory(transport.getBusinessCategory());
        entity.setBusinessDescription(transport.getBusinessDescription());
        entity.setPan(transport.getPan());
        entity.setArn(transport.getArn());
        entity.setArnIssueDate(transport.getArnIssueDate());
        entity.setSacCode(transport.getSacCode());

        return entity;
    }

    private static void addAddressToEntityModel(CompanyDto transport, Company company) {
        List<CompanyAddress> entityList = new ArrayList<>();
        transport.getAddress().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(dto, new CompanyAddress());
            entity.setCompany(company);
            entityList.add(entity);
        });
        company.getAddress().addAll(entityList);
    }

    private static void addGstToEntityModel(CompanyDto transport, Company company) {
        List<CompanyGst> gstList = new ArrayList<>();
        transport.getGst().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyGst entity = GstConverter.toEntityModel(dto);
            entity.setCompany(company);
            gstList.add(entity);
        });
        company.getGst().addAll(gstList);
    }

    private static void addAccountToEntityModel(CompanyDto transport, Company company) {
        List<Account> accountList = new ArrayList<>();
        transport.getAccount().stream().filter(Objects::nonNull).forEach(dto -> {
            Account entity = AccountConverter.toEntityModel(dto);
            entity.setCompany(company);
            accountList.add(entity);
        });
        company.getAccount().addAll(accountList);
    }

    private static void addThemeToEntityModel(CompanyDto transport, Company company) {
        List<Theme> themeList = new ArrayList<>();
        transport.getTheme().stream().filter(Objects::nonNull).forEach(dto -> {
            Theme entity = ThemeConverter.toEntityModel(dto);
            entity.setCompany(company);
            themeList.add(entity);
        });
        company.getTheme().addAll(themeList);
    }

    private static void addNotesToEntityModel(CompanyDto transport, Company company) {
        List<CompanyNotes> notesList = new ArrayList<>();
        transport.getNotes().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyNotes entity = CompanyNotesConverter.toEntityModel(dto);
            entity.setCompany(company);
            notesList.add(entity);
        });
        company.getCompanyNotes().addAll(notesList);
    }

    private static void addCompanyHolidayToEntityModel(CompanyDto transport, Company company) {
        List<CompanyHoliday> companyEventList = new ArrayList<>();
        transport.getCompanyHoliday().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyHoliday entity = CompanyHolidayConverter.toEntityModel(dto, new CompanyHoliday());
            entity.setCompany(company);
            companyEventList.add(entity);
        });
        company.getCompanyHoliday().addAll(companyEventList);
    }

    private static void addCompanyEventToEntityModel(CompanyDto transport, Company company) {
        List<CompanyEvent> companyEventList = new ArrayList<>();
        transport.getCompanyEvent().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyEvent entity = CompanyEventConverter.toEntityModel(dto, new CompanyEvent());
            entity.setCompany(company);
            companyEventList.add(entity);
        });
        company.getCompanyEvent().addAll(companyEventList);
    }

    private static void addPayrollComponentConfigurationToEntityModel(CompanyDto transport, Company company) {
        List<PayrollComponentConfiguration> payrollComponentConfigurationList = new ArrayList<>();
        transport.getPayrollComponentConfiguration().stream().filter(Objects::nonNull).forEach(dto -> {
            PayrollComponentConfiguration entity = PayrollComponentConfigurationConverter.toEntityModel(dto, new PayrollComponentConfiguration());
            entity.setCompany(company);
            payrollComponentConfigurationList.add(entity);
        });
        company.getPayrollComponentConfiguration().addAll(payrollComponentConfigurationList);
    }


}