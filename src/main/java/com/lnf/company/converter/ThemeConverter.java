/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.converter;

import com.lnf.company.model.Theme;
import com.lnf.company.model.enums.ThemeType;
import com.lnf.dto.company.ThemeDto;

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
