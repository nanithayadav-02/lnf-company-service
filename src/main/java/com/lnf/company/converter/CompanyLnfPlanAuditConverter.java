package com.lnf.company.converter;

import com.lnf.company.model.CompanyLnfPlanAudit;
import com.lnf.dto.company.CompanyLnfPlanAuditDto;

public class CompanyLnfPlanAuditConverter {
    private CompanyLnfPlanAuditConverter() {
    }

    public static CompanyLnfPlanAuditDto toTransportModel(CompanyLnfPlanAudit entity) {
        if (entity == null) {
            return null;
        }
        return CompanyLnfPlanAuditDto.builder()
                .id(entity.getId())
                .startDate(entity.getStartDate())
                .status(entity.getStatus())
                .companyPlanId(entity.getCompanyLnfPlan().getId())
                .build();
    }

    public static CompanyLnfPlanAudit toEntityModel(CompanyLnfPlanAuditDto transport, CompanyLnfPlanAudit entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setStatus(transport.getStatus());
        entity.setStartDate(transport.getStartDate());
        return entity;
    }

}
