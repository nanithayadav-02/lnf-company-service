package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.dto.CompanyDto;
import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.CompanyAddress;
import com.technofacts.lnf.company.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompanyConverter {

    public static CompanyDto toTransportModel(Company entity) {

        if (entity == null) {
            return null;
        }
        CompanyDto dto = CompanyDto.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .name(entity.getName())
                .status(entity.getStatus())
                .email(entity.getEmail())
                .telephone(entity.getTelephone())
                .mobile(entity.getMobile())
                .website(entity.getWebsite())
                .businessCategory(entity.getBusinessCategory())
                .businessDescription(entity.getBusinessDescription())
                .pan(entity.getPan())
                .arn(entity.getArn())
                .arnIssueDate(entity.getArnIssueDate())
                .sacCode(entity.getSacCode())
                .address(new ArrayList<>())
                .image(new ArrayList<>())
                .build();

        dto.getAddress().addAll(entity.getAddress().stream()
                .map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));
        dto.getImage().addAll(entity.getImage().stream()
                .map(ImageConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));

        return dto;
    }

    public static Company toEntityModel(CompanyDto transport) {
        if (transport == null) {
            return null;
        }
        Company entity = new Company();
        entity.setId(transport.getId());
        entity.setCompanyId(transport.getCompanyId());
        entity.setName(transport.getName());
        entity.setStatus(transport.getStatus());
        entity.setEmail(transport.getEmail());
        entity.setTelephone(transport.getTelephone());
        entity.setMobile(transport.getMobile());
        entity.setWebsite(transport.getWebsite());
        entity.setBusinessCategory(transport.getBusinessCategory());
        entity.setBusinessDescription(transport.getBusinessDescription());
        entity.setPan(transport.getPan());
        entity.setArn(transport.getArn());
        entity.setArnIssueDate(transport.getArnIssueDate());
        entity.setSacCode(transport.getSacCode());

        addAddressToEntityModel(transport, entity);
        imageToEntityModel(transport, entity);

        return entity;
    }

    private static void addAddressToEntityModel(CompanyDto transport, Company company) {
        List<CompanyAddress> entityList = new ArrayList<>();
        transport.getAddress().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyAddress entity = (CompanyAddress) AddressConverter.toEntityModel(dto, new CompanyAddress());
            entity.setCompany(company);
            entityList.add(entity);
        });
        company.getAddress().addAll(entityList);
    }

    private static void imageToEntityModel(CompanyDto transport, Company company) {
        List<Image> entityList = new ArrayList<>();
        transport.getImage().stream().filter(Objects::nonNull).forEach(dto -> {
            Image entity = ImageConverter.toEntityModel(dto, new Image());
            entity.setCompany(company);
            entityList.add(entity);
        });
        company.getImage().addAll(entityList);
    }

}
