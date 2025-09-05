/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.converter;

import com.lnf.company.model.*;
import com.lnf.dto.company.CompanyDto;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lnf.util.CommonUtils.safeConvert;

public class CompanyConverter {

    private CompanyConverter() {
    }

    public static CompanyDto toTransportModel(Company entity) {
        if (entity == null) {
            return null;
        }

        String planName = !CollectionUtils.isEmpty(entity.getCompanyPlans())
                ? entity.getCompanyPlans().stream()
                .map(companyPlan -> companyPlan.getPlan().getPlanName())
                .findFirst()
                .orElse(null)
                : null;

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
                .planName(planName)
                .address(safeConvert(entity.getAddress(), AddressConverter::toTransportModel))
                .gst(safeConvert(entity.getGst(), GstConverter::toTransportModel))
                .theme(safeConvert(entity.getTheme(), ThemeConverter::toTransportModel))
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
        addThemeToEntityModel(transport, entity);
        addNotesToEntityModel(transport, entity);
        addCompanyEventToEntityModel(transport, entity);
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
        if (transport.getAddress() != null) {
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
        if (transport.getGst() != null) {
            List<CompanyGst> gstList = new ArrayList<>();
            transport.getGst().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyGst entity = GstConverter.toEntityModel(dto);
                entity.setCompany(company);
                gstList.add(entity);
            });
            company.getGst().addAll(gstList);
        }
    }

    private static void addThemeToEntityModel(CompanyDto transport, Company company) {
        if (transport.getTheme() != null) {
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
        if (transport.getNotes() != null) {
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
        if (transport.getCompanyHoliday() != null) {
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
        if (transport.getCompanyEvent() != null) {
            List<CompanyEvent> companyEventList = new ArrayList<>();
            transport.getCompanyEvent().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyEvent entity = CompanyEventConverter.toEntityModel(dto, new CompanyEvent());
                entity.setCompany(company);
                companyEventList.add(entity);
            });
            company.getCompanyEvent().addAll(companyEventList);
        }
    }

}