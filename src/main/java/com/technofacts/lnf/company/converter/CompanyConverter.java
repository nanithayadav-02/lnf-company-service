package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.dto.CompanyDto;
import com.technofacts.lnf.company.model.*;

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
                .gst(new ArrayList<>())
                .account(new ArrayList<>())
                .build();

        dto.getAddress().addAll(entity.getAddress().stream()
                .map(AddressConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));
        dto.getImage().addAll(entity.getImage().stream()
                .map(ImageConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));
        dto.getGst().addAll(entity.getGst().stream().map(GstConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));
        dto.getAccount().addAll(entity.getAccount().stream().map(AccountConverter::toTransportModel)
                .filter(Objects::nonNull).collect(Collectors.toList()));

        return dto;
    }

    public static Company toEntityModel(CompanyDto transport) {
        Company entity = toEntityModel(transport, new Company());

        addAddressToEntityModel(transport, entity);
        imageToEntityModel(transport, entity);
        addGstToEntityModel(transport, entity);
        addAccountToEntityModel(transport, entity);

        return entity;
    }

    private static Company toEntityModel(CompanyDto transport, Company entity) {
        if (transport == null || entity == null) {
            return null;
        }

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

    private static void addGstToEntityModel(CompanyDto transport, Company company) {
        List<CompanyGst> gstList = new ArrayList<>();
        transport.getGst().stream().filter(Objects::nonNull).forEach(dto -> {
            CompanyGst entity = GstConverter.toEntityModel(dto);
            entity.setCompany(company);
            gstList.add(entity);
        });
        company.getGst().addAll(gstList);
    }

    private static void addAccountToEntityModel(CompanyDto transport, Company company) {
        List<Account> accountList = new ArrayList<>();
        transport.getAccount().stream().filter(Objects::nonNull).forEach(dto -> {
            Account entity = AccountConverter.toEntityModel(dto);
            entity.setCompany(company);
            accountList.add(entity);
        });
        company.getAccount().addAll(accountList);
    }

}
