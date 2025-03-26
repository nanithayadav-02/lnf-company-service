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

import com.lnf.company.service.CompanyEventService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyEventDto;
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
public class CompanyEventController {

    private final CompanyEventService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/event")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/event")
    public List<CompanyEventDto> findByCompanyId(@PathVariable final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/event/{eventId}")
    public CompanyEventDto findById(@PathVariable final UUID companyId,
                                    @PathVariable final UUID eventId) {
        return service.findById(companyId, eventId);
    }

    @PostMapping(value = "/company/{companyId}/event")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId,
                       @RequestBody final CompanyEventDto resource) {
        service.create(companyId, resource);
    }

    @PostMapping(value = "/company/{companyId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId,
                       @RequestBody final List<CompanyEventDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyId, @PathVariable final UUID eventId,
                       @RequestBody final CompanyEventDto resource) {
        service.update(companyId, eventId, resource);
    }


    @DeleteMapping(value = "/company/{companyId}/events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId, @PathVariable final UUID eventId) {
        service.deleteById(companyId, eventId);
    }

    @PostMapping("/company/event/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public void clearCaches() {
        service.clearCaches();
    }

}
