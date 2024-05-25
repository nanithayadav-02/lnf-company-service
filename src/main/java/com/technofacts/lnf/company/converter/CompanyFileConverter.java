package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyFile;
import com.technofacts.lnf.dto.company.CompanyFileDto;

public class CompanyFileConverter {

    public static CompanyFileDto toTransportModel(CompanyFile entity) {

        if (entity == null) {
            return null;
        }

        return CompanyFileDto.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .description(entity.getDescription())
                .build();

    }

    public static CompanyFile toEntityModel(CompanyFileDto transport, CompanyFile entity) {

        if (transport == null || entity == null) {
            return null;
        }

        entity.setFileName(transport.getFileName());
        entity.setDescription(transport.getDescription());
        entity.setId(transport.getId());
        return entity;

    }

}
