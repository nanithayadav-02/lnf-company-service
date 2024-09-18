package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyPolicy;
import com.technofacts.lnf.dto.company.CompanyPolicyDto;
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
