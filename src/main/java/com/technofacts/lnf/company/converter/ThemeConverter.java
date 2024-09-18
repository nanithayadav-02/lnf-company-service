package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.Theme;
import com.technofacts.lnf.company.model.enums.ThemeType;
import com.technofacts.lnf.dto.company.ThemeDto;

public class ThemeConverter {

    private ThemeConverter() {
    }

    public static ThemeDto toTransportModel(Theme entity) {
        if (entity == null) {
            return null;
        }

        return ThemeDto.builder()
                .id(entity.getId())
                .type(entity.getType().name())
                .value(entity.getValue())
                .build();
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
