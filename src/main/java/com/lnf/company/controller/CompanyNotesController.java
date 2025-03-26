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

package com.lnf.company.controller;

import com.lnf.company.service.CompanyNotesService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.NotesDto;
import com.lnf.service.common.page.PageableAsQueryParam;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyNotesController {

    private final CompanyNotesService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/notes")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/notes")
    public List<NotesDto> findByCompanyId(@PathVariable final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/notes/{notesID}")
    public NotesDto findById(@PathVariable final UUID companyId, @PathVariable final UUID notesID) {
        return service.findById(companyId, notesID);
    }

    @PostMapping(value = "/company/{companyId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId, @RequestBody final List<NotesDto> resource) {
        service.create(companyId, resource);
    }

    @PostMapping(value = "/company/{companyId}/note")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId, @RequestBody final NotesDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/notes/{notesId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyId, @PathVariable final UUID notesId,
                       @RequestBody final NotesDto resource) {
        service.update(companyId, notesId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/notes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/notes/{notesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId, @PathVariable final UUID notesId) {
        service.deleteById(companyId, notesId);
    }

    @PostMapping("/company/notes/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public void clearCaches() {
        service.clearCaches();
    }
}
