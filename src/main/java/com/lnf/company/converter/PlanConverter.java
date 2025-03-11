package com.lnf.company.converter;

import com.lnf.company.model.CompanyPlan;
import com.lnf.company.model.Plan;
import com.lnf.dto.company.PlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanConverter {
    private PlanConverter() {
    }

    public static PlanDto toTransportModel(Plan entity) {
        if (entity == null) {
            return null;
        }
        PlanDto dto = new PlanDto();
        dto.setId(entity.getId());
        dto.setPlanName(entity.getPlanName());
        dto.setDescription(entity.getDescription());
        dto.setCompanyPlans(new ArrayList<>());
        dto.getCompanyPlans().addAll(entity.getCompanyLnfPlans().stream().
                map(CompanyPlanConverter::toTransportModel).filter(Objects::nonNull).toList());

        return dto;
    }

    public static Plan toEntityModel(PlanDto transport, Plan entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setPlanName(transport.getPlanName());
        entity.setDescription(transport.getDescription());
        addCompanyPlanToEntityModel(transport, entity);
        return entity;
    }

    private static void addCompanyPlanToEntityModel(PlanDto transport, Plan lnfPlan) {
        if (transport.getCompanyPlans() != null) {
            List<CompanyPlan> companyLnfPlans = new ArrayList<>();
            transport.getCompanyPlans().stream().filter(Objects::nonNull).forEach(dto -> {
                CompanyPlan entity = CompanyPlanConverter.toEntityModel(dto, new CompanyPlan());
                entity.setPlan(lnfPlan);
                companyLnfPlans.add(entity);
            });
            lnfPlan.getCompanyLnfPlans().addAll(companyLnfPlans);
        }
    }

}
