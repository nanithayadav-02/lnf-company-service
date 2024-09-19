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


import com.lnf.company.model.CompanyEvent;
import com.lnf.company.model.enums.EventType;
import com.lnf.dto.company.CompanyEventDto;

public class CompanyEventConverter {

    private CompanyEventConverter() {
    }

    public static CompanyEventDto toTransportModel(CompanyEvent entity) {
        return CompanyEventDto.builder()
                .id(entity.getId())
                .eventType(entity.getEventType().name())
                .dateAndTime(entity.getDateAndTime())
                .assignTo(entity.getAssignTo())
                .eventDescription(entity.getEventDescription())
                .status(entity.getStatus()).build();

    }

    public static CompanyEvent toEntityModel(CompanyEventDto transport, CompanyEvent entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(entity.getId());
        entity.setEventType(EventType.valueOf(transport.getEventType()));
        entity.setEventDescription(transport.getEventDescription());
        entity.setAssignTo(transport.getAssignTo());
        entity.setDateAndTime(transport.getDateAndTime());
        entity.setStatus(transport.getStatus());
        return entity;
    }

}
