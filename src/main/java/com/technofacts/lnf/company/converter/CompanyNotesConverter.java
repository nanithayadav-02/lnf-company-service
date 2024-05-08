package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyNotes;
import com.technofacts.lnf.dto.company.NotesDto;

public class CompanyNotesConverter {

    private CompanyNotesConverter() {
    }

    public static NotesDto toTransportModel(CompanyNotes entity) {
        NotesDto notesDto = new NotesDto();
        notesDto.setId(entity.getId());
        notesDto.setNotes(entity.getNotes());
        notesDto.setCreatedBy(entity.getCreatedBy());
        notesDto.setCreatedTime(entity.getCreatedTime());
        notesDto.setLastUpdatedBy(entity.getLastUpdatedBy());
        notesDto.setLastUpdatedTime(entity.getLastUpdatedTime());
        notesDto.setId(entity.getId());
        return notesDto;

    }

    public static CompanyNotes toEntityModel(NotesDto transport) {
        return toEntityModel(transport, new CompanyNotes());
    }

    public static CompanyNotes toEntityModel(NotesDto transport, CompanyNotes entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setId(transport.getId());
        entity.setNotes(transport.getNotes());
        entity.setCreatedBy(transport.getCreatedBy());
        entity.setCreatedTime(transport.getCreatedTime());
        entity.setLastUpdatedBy(transport.getLastUpdatedBy());
        entity.setLastUpdatedTime(transport.getLastUpdatedTime());
        return entity;
    }

}
