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

import com.lnf.company.model.Address;
import com.lnf.company.model.enums.AddressType;
import com.lnf.dto.company.AddressDto;

public class AddressConverter {

    private AddressConverter() {
    }

    public static AddressDto toTransportModel(Address entity) {
        if (entity == null) {
            return null;
        }

        return AddressDto.builder()
                .id(entity.getId())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .town(entity.getTown())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .countryCode(entity.getCountryCode())
                .postCode(entity.getPostCode())
                .type(entity.getType().name())
                .build();
    }

    public static Address toEntityModel(AddressDto transport, Address entity) {

        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setAddressLine1(transport.getAddressLine1());
        entity.setAddressLine2(transport.getAddressLine2());
        entity.setTown(transport.getTown());
        entity.setCity(transport.getCity());
        entity.setState(transport.getState());
        entity.setCountry(transport.getCountry());
        entity.setCountryCode(transport.getCountryCode());
        entity.setPostCode(transport.getPostCode());
        entity.setType(AddressType.valueOf(transport.getType()));

        return entity;
    }

}
