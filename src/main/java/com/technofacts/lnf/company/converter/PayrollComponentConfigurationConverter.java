package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.PayrollComponentConfiguration;
import com.technofacts.lnf.company.model.enums.ComponentStatus;
import com.technofacts.lnf.dto.company.PayrollComponentConfigurationDto;

public class PayrollComponentConfigurationConverter {

    public static PayrollComponentConfigurationDto toTransportModel(PayrollComponentConfiguration entity) {
       return PayrollComponentConfigurationDto.builder()
                .componentName(entity.getComponentName())
                .componentValue(entity.getComponentValue())
                .componentCode(entity.getComponentCode())
                .percentage(entity.getPercentage())
                .financialYear(entity.getFinancialYear())
                .componentStatus(entity.getComponentStatus().name())
                .build();
    }

    public static PayrollComponentConfiguration toEntityModel(PayrollComponentConfigurationDto transport, PayrollComponentConfiguration entity) {
        if (transport == null) {
            return null;
        }
        entity.setComponentName(transport.getComponentName());
        entity.setComponentValue(transport.getComponentValue());
        entity.setComponentCode(transport.getComponentCode());
        entity.setFinancialYear(transport.getFinancialYear());
        entity.setPercentage(transport.getPercentage());
        entity.setComponentStatus(ComponentStatus.valueOf(transport.getComponentStatus()));
        return entity;
    }
}

