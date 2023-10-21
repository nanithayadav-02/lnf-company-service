
package com.technofacts.lnf.company.converter;

import java.io.IOException;

import com.technofacts.lnf.company.model.Image;
import com.technofacts.lnf.dto.company.ImageDto;
import org.springframework.web.multipart.MultipartFile;

public class ImageConverter {

    public static ImageDto toTransportModel(Image entity) {
        if (entity == null) {
            return null;
        }
        ImageDto dto = new ImageDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setContentType(entity.getContentType());
        dto.setSize(entity.getSize());
        dto.setUrl(entity.getUrl());

        return dto;
    }

    public static Image toEntityModel(MultipartFile transport, boolean awsS3BucketEnabled, String filePath) throws IOException {
        if (transport == null) {
            return null;
        }
        Image entity = new Image();
        return toEntityModel(transport, entity,awsS3BucketEnabled, filePath);

    }

    public static Image toEntityModel(MultipartFile transport, Image entity,boolean awsS3BucketEnabled,
                                      String filePath) throws IOException {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setName(transport.getOriginalFilename() != null ? transport.getOriginalFilename() : transport.getName());
        entity.setContentType(transport.getContentType());
        entity.setSize(transport.getSize());
        entity.setContent(awsS3BucketEnabled ? new byte[0] : transport.getBytes());


        return entity;
    }

    public static Image toEntityModel(ImageDto transport, Image entity,boolean awsS3BucketEnabled,
                                      String filePath) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setName(transport.getName());
        entity.setContentType(transport.getContentType());
        entity.setSize(transport.getSize());
        entity.setContent(transport.getContent());
        entity.setUrl(filePath);

        return entity;
    }
}