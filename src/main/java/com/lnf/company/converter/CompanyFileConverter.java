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

import com.lnf.company.model.CompanyFile;
import com.lnf.dto.company.CompanyFileDto;

public class CompanyFileConverter {

    private CompanyFileConverter() {
    }

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
