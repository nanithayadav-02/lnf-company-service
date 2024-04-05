package com.technofacts.lnf.company.converter;

import com.technofacts.lnf.company.model.CompanyNotes;
import com.technofacts.lnf.dto.company.NotesDto;

public class CompanyNotesConverter {
    public static NotesDto toTransportModel(CompanyNotes entity) {
        if (entity == null) {
            return null;
        }
        return NotesDto.builder()
                .id(entity.getId())
                .notes(entity.getNotes())
                .build();
    }

    public static CompanyNotes toEntityModel(NotesDto transport) {
        return toEntityModel(transport, new CompanyNotes());
    }

    public static CompanyNotes toEntityModel(NotesDto transport, CompanyNotes entity) {
        if (transport == null || entity == null) {
            return null;
        }
        entity.setNotes(transport.getNotes());
        return entity;
    }
}
