package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.dto.ThemeDto;
import com.technofacts.lnf.company.model.Theme;
import com.technofacts.lnf.company.model.enums.ThemeType;

public class ThemeConverter {

    public static ThemeDto toTransportModel(Theme entity) {
        if (entity == null) {
            return null;
        }
        ThemeDto dto = ThemeDto.builder()
                .id(entity.getId())
                .type(entity.getType().name())
                .value(entity.getValue())
                .build();
        return dto;
    }

    public static Theme toEntityModel(ThemeDto transport) {
        if (transport == null) {
            return null;
        }

        Theme entity = new Theme();
        entity.setId(transport.getId());
        entity.setType(ThemeType.valueOf(transport.getType()));
        entity.setValue(transport.getValue());

        return entity;
    }

}
