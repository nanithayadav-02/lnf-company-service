package com.lnf.company.converter;

import com.lnf.company.model.CompanyPlanAudit;
import com.lnf.dto.company.CompanyPlanAuditDto;

public class CompanyPlanAuditConverter {
    private CompanyPlanAuditConverter() {
    }

    public static CompanyPlanAuditDto toTransportModel(CompanyPlanAudit entity) {
        if (entity == null) {
            return null;
        }
        return CompanyPlanAuditDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .status(entity.getStatus())
                .companyPlanId(entity.getCompanyPlan().getId())
                .build();
    }

    public static CompanyPlanAudit toEntityModel(CompanyPlanAuditDto transport, CompanyPlanAudit entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setStatus(transport.getStatus());
        entity.setStartDate(transport.getStartDate());
        return entity;
    }

}
