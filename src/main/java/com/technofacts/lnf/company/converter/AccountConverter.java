package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.Account;
import com.technofacts.lnf.dto.company.AccountDto;

public class AccountConverter {

    public static AccountDto toTransportModel(Account entity) {

        if (entity == null) {
            return null;
        }

        return AccountDto.builder()
                .id(entity.getId())
                .branch(entity.getBranch())
                .ibanNumber(entity.getIbanNumber())
                .ifscCode(entity.getIfscCode())
                .number(entity.getNumber())
                .address(entity.getAddress())
                .build();
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
