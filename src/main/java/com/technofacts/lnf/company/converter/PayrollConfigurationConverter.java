package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.PayrollConfiguration;
import com.technofacts.lnf.company.model.enums.ComponentStatus;
import com.technofacts.lnf.dto.company.PayrollConfigurationDto;

public class PayrollConfigurationConverter {

    public static PayrollConfigurationDto toTransportModel(PayrollConfiguration entity) {
        PayrollConfigurationDto payrollConfigurationDto = PayrollConfigurationDto.builder()
                .id(entity.getId())
                .componentName(entity.getComponentName())
                .componentValue(entity.getComponentValue())
                .componentCode(entity.getComponentCode())
                .percentage(entity.getPercentage())
                .financialYear(entity.getFinancialYear())
                .componentStatus(entity.getComponentStatus().name())
                .build();
        return payrollConfigurationDto;
    }

    public static PayrollConfiguration toEntityModel(PayrollConfigurationDto transport, PayrollConfiguration entity) {
        if (transport == null) {
            return null;
        }

        if (entity == null || entity.getId() == null) {
            entity = new PayrollConfiguration();
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

