package com.lnf.company.converter;

import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.CompanyPlanAudit;
import com.lnf.company.model.enums.CompanyPlanStatus;
import com.lnf.dto.company.CompanyPlanAuditDto;
import com.lnf.dto.company.CompanyPlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompanyPlanConverter {

    private CompanyPlanConverter() {
    }

    public static CompanyPlanDto toTransportModel(CompanyPlan entity) {
        if (entity == null) {
            return null;
        }

        return CompanyPlanDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus().name())
                .companyId(entity.getCompany().getId())
                .lnfPlanId(entity.getPlan().getId())
                .companyPlanAudits(entity.getCompanyPlanAudits().stream().map(companyPlan -> toTransportModel(companyPlan, entity)).toList()).build();
    }

    public static CompanyPlanAuditDto toTransportModel(CompanyPlanAudit entity, CompanyPlan companyPlan) {
        if (entity == null) {
            return null;
        }
        return CompanyPlanAuditDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .companyPlanId(entity.getCompanyPlan().getId())
                .planName(companyPlan.getPlan().getPlanName())
                .description(companyPlan.getPlan().getDescription())
                .build();
    }

    public static CompanyPlan toEntityModel(CompanyPlanDto transport, CompanyPlan entity) {

        if (transport == null || entity == null) {
            return null;
        }

        entity.setId(transport.getId());
        entity.setStartDate(transport.getStartDate());
        entity.setEndDate(transport.getEndDate());
        entity.setStatus(CompanyPlanStatus.valueOf(transport.getStatus()));
        addCompanyPlanAuditToEntityModel(transport, entity);
        return entity;
    }

    private static void addCompanyPlanAuditToEntityModel(CompanyPlanDto transport, CompanyPlan companyPlan) {
        if (transport.getCompanyPlanAudits() != null) {
            List<CompanyPlanAudit> companyPlans = new ArrayList<>();
            transport.getCompanyPlanAudits().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyPlanAudit entity = CompanyPlanAuditConverter.toEntityModel(dto, new CompanyPlanAudit());
                entity.setCompanyPlan(companyPlan);
                companyPlans.add(entity);
            });
            companyPlan.getCompanyPlanAudits().addAll(companyPlans);
        }
    }

}
