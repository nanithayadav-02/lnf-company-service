package com.lnf.company.converter;

import com.lnf.company.model.CompanyLnfPlan;
import com.lnf.company.model.LnfPlan;
import com.lnf.dto.company.LnfPlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LnfPlanConverter {
    private LnfPlanConverter() {
    }

    public static LnfPlanDto toTransportModel(LnfPlan entity) {
        if (entity == null) {
            return null;
        }
        LnfPlanDto dto = new LnfPlanDto();
        dto.setId(entity.getId());
        dto.setPlanName(entity.getPlanName());
        dto.setDescription(entity.getDescription());
        dto.setCompanyLnfPlans(new ArrayList<>());
        dto.getCompanyLnfPlans().addAll(entity.getCompanyLnfPlans().stream().
                map(CompanyLnfPlanConverter::toTransportModel).filter(Objects::nonNull).toList());

        return dto;
    }

    public static LnfPlan toEntityModel(LnfPlanDto transport, LnfPlan entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setPlanName(transport.getPlanName());
        entity.setDescription(transport.getDescription());
        addCompanyLnfPlanToEntityModel(transport, entity);
        return entity;
    }

    private static void addCompanyLnfPlanToEntityModel(LnfPlanDto transport, LnfPlan lnfPlan) {
        if (transport.getCompanyLnfPlans() != null) {
            List<CompanyLnfPlan> companyLnfPlans = new ArrayList<>();
            transport.getCompanyLnfPlans().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyLnfPlan entity = CompanyLnfPlanConverter.toEntityModel(dto, new CompanyLnfPlan());
                entity.setLnfPlan(lnfPlan);
                companyLnfPlans.add(entity);
            });
            lnfPlan.getCompanyLnfPlans().addAll(companyLnfPlans);
        }
    }

}
