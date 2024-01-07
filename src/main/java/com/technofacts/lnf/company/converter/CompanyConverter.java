package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.*;
import com.technofacts.lnf.dto.company.CompanyDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.technofacts.lnf.util.CommonUtils.safeConvert;

public class CompanyConverter {

    private CompanyConverter() {

    }

    public static CompanyDto toTransportModel(Company entity) {
        if (entity == null) {
            return null;
        }

        return CompanyDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .status(entity.getStatus())
                .email(entity.getEmail())
                .telephone(entity.getTelephone())
                .mobile(entity.getMobile())
                .website(entity.getWebsite())
                .businessCategory(entity.getBusinessCategory())
                .businessDescription(entity.getBusinessDescription())
                .pan(entity.getPan())
                .arn(entity.getArn())
                .arnIssueDate(entity.getArnIssueDate())
                .sacCode(entity.getSacCode())
                .address(safeConvert(entity.getAddress(), AddressConverter::toTransportModel))
                .gst(safeConvert(entity.getGst(), GstConverter::toTransportModel))
                .account(safeConvert(entity.getAccount(), AccountConverter::toTransportModel))
                .theme(safeConvert(entity.getTheme(), ThemeConverter::toTransportModel))
                .notes(safeConvert(entity.getCompanyNotes(), CompanyNotesConverter::toTransportModel))
                .companyEvent(safeConvert(entity.getCompanyEvent(), CompanyEventConverter::toTransportModel))
                .payrollComponentConfiguration(safeConvert(entity.getPayrollComponentConfiguration(), PayrollComponentConfigurationConverter::toTransportModel))
                .companyHoliday(safeConvert(entity.getCompanyHoliday(), CompanyHolidayConverter::toTransportModel))
                .build();
    }


    public static Company toEntityModel(CompanyDto transport) {
        if (transport == null) {
            return null;
        }
        Company entity = new Company();
        toEntityModel(transport, entity);

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

    private static void toEntityModel(CompanyDto transport, Company entity) {
        if (transport == null || entity == null) {
            return;
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
    }


    private static void addAddressToEntityModel(CompanyDto transport, Company company) {
        if(transport.getAddress() != null) {
        List<CompanyAddress> entityList = new ArrayList<>();
        transport.getAddress().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(dto, new CompanyAddress());
            entity.setCompany(company);
            entityList.add(entity);
        });
        company.getAddress().addAll(entityList);
    }
    }

    private static void addGstToEntityModel(CompanyDto transport, Company company) {
        if(transport.getGst() != null) {
        List<CompanyGst> gstList = new ArrayList<>();
        transport.getGst().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyGst entity = GstConverter.toEntityModel(dto);
            entity.setCompany(company);
            gstList.add(entity);
        });
        company.getGst().addAll(gstList);
    }
    }

    private static void addAccountToEntityModel(CompanyDto transport, Company company) {
         if(transport.getAccount() != null) {
        List<Account> accountList = new ArrayList<>();
        transport.getAccount().stream().filter(Objects::nonNull).forEach(dto -> {
            Account entity = AccountConverter.toEntityModel(dto);
            entity.setCompany(company);
            accountList.add(entity);
        });
        company.getAccount().addAll(accountList);
    }
    }

    private static void addThemeToEntityModel(CompanyDto transport, Company company) {
        if(transport.getTheme() != null) {
        List<Theme> themeList = new ArrayList<>();
        transport.getTheme().stream().filter(Objects::nonNull).forEach(dto -> {
            Theme entity = ThemeConverter.toEntityModel(dto);
            entity.setCompany(company);
            themeList.add(entity);
        });
        company.getTheme().addAll(themeList);
    }
    }

    private static void addNotesToEntityModel(CompanyDto transport, Company company) {
        if(transport.getNotes() != null) {
        List<CompanyNotes> notesList = new ArrayList<>();
        transport.getNotes().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyNotes entity = CompanyNotesConverter.toEntityModel(dto);
            entity.setCompany(company);
            notesList.add(entity);
        });
        company.getCompanyNotes().addAll(notesList);
    }
    }

    private static void addCompanyHolidayToEntityModel(CompanyDto transport, Company company) {
        if(transport.getCompanyHoliday() != null) {
        List<CompanyHoliday> companyEventList = new ArrayList<>();
        transport.getCompanyHoliday().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyHoliday entity = CompanyHolidayConverter.toEntityModel(dto, new CompanyHoliday());
            entity.setCompany(company);
            companyEventList.add(entity);
        });
        company.getCompanyHoliday().addAll(companyEventList);
    }
    }

    private static void addCompanyEventToEntityModel(CompanyDto transport, Company company) {
        if(transport.getCompanyEvent() != null) {
        List<CompanyEvent> companyEventList = new ArrayList<>();
        transport.getCompanyEvent().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyEvent entity = CompanyEventConverter.toEntityModel(dto, new CompanyEvent());
            entity.setCompany(company);
            companyEventList.add(entity);
        });
        company.getCompanyEvent().addAll(companyEventList);
    }
    }

    private static void addPayrollComponentConfigurationToEntityModel(CompanyDto transport, Company company) {
         if(transport.getPayrollComponentConfiguration() != null) {
        List<PayrollComponentConfiguration> payrollComponentConfigurationList = new ArrayList<>();
        transport.getPayrollComponentConfiguration().stream().filter(Objects::nonNull).forEach(dto -> {
            PayrollComponentConfiguration entity = PayrollComponentConfigurationConverter.toEntityModel(dto, new PayrollComponentConfiguration());
            entity.setCompany(company);
            payrollComponentConfigurationList.add(entity);
        });
        company.getPayrollComponentConfiguration().addAll(payrollComponentConfigurationList);
    }
    }
}