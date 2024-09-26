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

import com.lnf.company.service.CompanyService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyDto;
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
public class CompanyController {

    private final CompanyService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company", params = {"search"})
    public List<CompanyDto> search(@RequestParam(value = "search") String search) {
        return service.findAll(search);
    }

    @GetMapping(value = "/company/{companyCode}")
    public CompanyDto findCompany(@PathVariable("companyCode") final String companyCode) {
        return service.findByCompanyCode(companyCode);
    }

    @PostMapping(value = "/company")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final CompanyDto resource) {
        service.create(resource);
    }

    @PutMapping(value = "/company/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @RequestBody final CompanyDto resource) {
        service.update(companyId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.delete(companyId);
    }

}
