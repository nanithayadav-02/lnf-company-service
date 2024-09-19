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

import com.lnf.company.model.CompanyPolicy;
import com.lnf.dto.company.CompanyPolicyDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class CompanyPolicyConverter {

    private CompanyPolicyConverter() {
    }

    public static CompanyPolicyDto toTransportModel(CompanyPolicy entity) {
        if (entity == null) {
            return null;
        }
        CompanyPolicyDto dto = new CompanyPolicyDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContentType(entity.getContentType());
        dto.setSize(entity.getSize());

        return dto;
    }

    public static CompanyPolicy toEntityModel(MultipartFile transport, boolean awsS3BucketEnabled) throws IOException {
        return toEntityModel(transport, new CompanyPolicy(),awsS3BucketEnabled);
    }

    public static CompanyPolicy toEntityModel(MultipartFile transport, CompanyPolicy entity,boolean awsS3BucketEnabled) throws IOException {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setName(transport.getOriginalFilename() != null ? transport.getOriginalFilename() : transport.getName());
        entity.setContentType(transport.getContentType());
        entity.setSize(transport.getSize());
        entity.setContent(awsS3BucketEnabled ? new byte[0] : transport.getBytes());

        return entity;
    }
}
