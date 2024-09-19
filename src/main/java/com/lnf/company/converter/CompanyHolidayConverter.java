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

import com.lnf.company.model.CompanyHoliday;
import com.lnf.dto.company.CompanyHolidayDto;

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
