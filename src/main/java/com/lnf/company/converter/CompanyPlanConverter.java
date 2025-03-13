package com.lnf.company.converter;

import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.CompanyPlanAudit;
import com.lnf.company.model.enums.CompanyPlanStatus;
import com.lnf.dto.company.CompanyPlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lnf.util.CommonUtils.safeConvert;

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
                .companyPlanAudits(safeConvert(entity.getCompanyPlanAudits(), CompanyPlanAuditConverter::toTransportModel))
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

    private static void addCompanyPlanAuditToEntityModel(CompanyPlanDto transport, CompanyPlan CompanyPlan) {
        if (transport.getCompanyPlanAudits() != null) {
            List<CompanyPlanAudit> CompanyPlans = new ArrayList<>();
            transport.getCompanyPlanAudits().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyPlanAudit entity = CompanyPlanAuditConverter.toEntityModel(dto, new CompanyPlanAudit());
                entity.setCompanyPlan(CompanyPlan);
                CompanyPlans.add(entity);
            });
            CompanyPlan.getCompanyPlanAudits().addAll(CompanyPlans);
        }
    }

}
