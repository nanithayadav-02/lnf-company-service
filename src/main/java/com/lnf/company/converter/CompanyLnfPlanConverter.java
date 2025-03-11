package com.lnf.company.converter;

import com.lnf.company.model.CompanyLnfPlan;
import com.lnf.company.model.CompanyLnfPlanAudit;
import com.lnf.company.model.enums.CompanyLnfPlanStatus;
import com.lnf.dto.company.CompanyLnfPlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lnf.util.CommonUtils.safeConvert;

public class CompanyLnfPlanConverter {

    private CompanyLnfPlanConverter() {
    }

    public static CompanyLnfPlanDto toTransportModel(CompanyLnfPlan entity) {
        if (entity == null) {
            return null;
        }

        return CompanyLnfPlanDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .status(entity.getStatus().name())
                .companyId(entity.getCompany().getId())
                .lnfPlanId(entity.getLnfPlan().getId())
                .companyLnfPlanAudits(safeConvert(entity.getCompanyLnfPlanAudits(), CompanyLnfPlanAuditConverter::toTransportModel))
                .build();
    }

    public static CompanyLnfPlan toEntityModel(CompanyLnfPlanDto transport, CompanyLnfPlan entity) {

        if (transport == null || entity == null) {
            return null;
        }

        entity.setId(transport.getId());
        entity.setStartDate(transport.getStartDate());
        entity.setStatus(CompanyLnfPlanStatus.valueOf(transport.getStatus()));
        addCompanyLnfPlanAuditToEntityModel(transport, entity);
        return entity;
    }

    private static void addCompanyLnfPlanAuditToEntityModel(CompanyLnfPlanDto transport, CompanyLnfPlan companyLnfPlan) {
        if (transport.getCompanyLnfPlanAudits() != null) {
            List<CompanyLnfPlanAudit> companyLnfPlans = new ArrayList<>();
            transport.getCompanyLnfPlanAudits().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyLnfPlanAudit entity = CompanyLnfPlanAuditConverter.toEntityModel(dto, new CompanyLnfPlanAudit());
                entity.setCompanyLnfPlan(companyLnfPlan);
                companyLnfPlans.add(entity);
            });
            companyLnfPlan.getCompanyLnfPlanAudits().addAll(companyLnfPlans);
        }
    }

}
