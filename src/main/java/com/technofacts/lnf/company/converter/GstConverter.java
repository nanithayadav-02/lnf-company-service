package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyGst;
import com.technofacts.lnf.dto.company.GstDto;

public class GstConverter {

    public static GstDto toTransportModel(CompanyGst entity) {
        if (entity == null) {
            return null;
        }

        return GstDto.builder()
                .id(entity.getId())
                .location(entity.getLocation())
                .number(entity.getNumber())
                .build();
    }

    public static CompanyGst toEntityModel(GstDto transport) {

        if (transport == null) {
            return null;
        }

        CompanyGst entity = new CompanyGst();
        entity.setId(transport.getId());
        entity.setLocation(transport.getLocation());
        entity.setNumber(transport.getNumber());

        return entity;
    }

}
