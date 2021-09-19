package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.dto.CompanyDto;
import com.technofacts.lnf.company.model.Company;

public class CompanyConverter {

    public static CompanyDto toTransportModel(Company entity) {

        if (entity == null) {
            return null;
        }
        CompanyDto dto = new CompanyDto();
        dto.setId(entity.getId());
        dto.setCompanyId(entity.getCompanyId());
        return dto;
    }

    public static Company toEntityModel(CompanyDto transport) {
        if (transport == null) {
            return null;
        }
        Company entity = new Company();
        entity.setId(transport.getId());
        entity.setCompanyId(transport.getCompanyId());
        return entity;
    }

}
