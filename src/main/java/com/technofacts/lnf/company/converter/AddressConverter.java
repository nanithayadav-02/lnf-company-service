package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.Address;
import com.technofacts.lnf.company.model.enums.AddressType;
import com.technofacts.lnf.dto.company.AddressDto;

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
        entity.setPostCode(transport.getPostCode());
        entity.setType(AddressType.valueOf(transport.getType()));

        return entity;
    }

}
