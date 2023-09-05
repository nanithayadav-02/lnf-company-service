package com.technofacts.lnf.company.converter;


import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.dto.company.CompanyEventDto;

public class CompanyEventConverter {

    public static CompanyEventDto toTransportModel(CompanyEvent entity) {
        CompanyEventDto companyEventDto = CompanyEventDto.builder().id(entity.getId()).eventType(entity.getEventType().name()).dateAndTime(entity.getDateAndTime()).assignTo(entity.getAssignTo()).eventDescription(entity.getEventDescription()).build();
        return companyEventDto;

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
        return entity;
    }

}
