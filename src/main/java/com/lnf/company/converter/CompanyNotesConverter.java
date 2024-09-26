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

import com.lnf.company.model.CompanyNotes;
import com.lnf.dto.company.NotesDto;

public class CompanyNotesConverter {

    private CompanyNotesConverter() {
    }

    public static NotesDto toTransportModel(CompanyNotes entity) {
        if (entity == null) {
            return null;
        }

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

        if (transport.getCreatedBy() != null) {
            entity.setCreatedBy(transport.getCreatedBy());
        }
        if (transport.getCreatedTime() != null) {
            entity.setCreatedTime(transport.getCreatedTime());
        }
        entity.setLastUpdatedBy(transport.getLastUpdatedBy());
        entity.setLastUpdatedTime(transport.getLastUpdatedTime());

        return entity;
    }

}
