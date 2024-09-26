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

import com.lnf.company.model.CompanyGst;
import com.lnf.dto.company.GstDto;

public class GstConverter {

    private GstConverter() {
    }

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
