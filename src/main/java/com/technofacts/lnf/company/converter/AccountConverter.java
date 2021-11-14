package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.dto.AccountDto;
import com.technofacts.lnf.company.model.Account;

public class AccountConverter {

    public static AccountDto toTransportModel(Account entity) {

        if (entity == null) {
            return null;
        }
        AccountDto dto = AccountDto.builder()
                .id(entity.getId())
                .branch(entity.getBranch())
                .ibanNumber(entity.getIbanNumber())
                .ifscCode(entity.getIfscCode())
                .number(entity.getNumber())
                .address(entity.getAddress())
                .build();

        return dto;
    }

    public static Account toEntityModel(AccountDto transport) {

        if (transport == null) {
            return null;
        }

        Account entity = new Account();
        entity.setId(transport.getId());
        entity.setNumber(transport.getNumber());
        entity.setBranch(transport.getBranch());
        entity.setIfscCode(transport.getIfscCode());
        entity.setIbanNumber(transport.getIbanNumber());
        entity.setAddress(transport.getAddress());

        return entity;
    }

}
