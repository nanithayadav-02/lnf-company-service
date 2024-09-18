package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyHoliday;
import com.technofacts.lnf.dto.company.CompanyHolidayDto;

public class CompanyHolidayConverter {

    private CompanyHolidayConverter() {
    }

    public static CompanyHolidayDto toTransportModel(CompanyHoliday entity) {
        if (entity == null) {
            return null;
        }

        return CompanyHolidayDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .deleted(entity.isDeleted())
                .location(entity.getLocation())
                .description(entity.getDescription())
                .build();
    }

    public static CompanyHoliday toEntityModel(CompanyHolidayDto transport, CompanyHoliday entity) {

        if (transport == null || entity == null) {
            return null;
        }

        entity.setId(transport.getId());
        entity.setDate(transport.getDate());
        entity.setDeleted(transport.isDeleted());
        entity.setDescription(transport.getDescription());
        entity.setLocation(transport.getLocation());

        return entity;
    }

}
