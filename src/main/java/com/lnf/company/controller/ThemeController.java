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

import java.util.List;
import java.util.UUID;

import com.lnf.company.service.ThemeService;
import com.lnf.dto.company.ThemeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class ThemeController {

    private final ThemeService service;

    @GetMapping(value = "/company/{companyId}/theme")
    public List<ThemeDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/theme/{themeId}")
    public ThemeDto findById(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId) {
        return service.findById(companyId, themeId);
    }

    @PostMapping(value = "/company/{companyId}/theme")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final List<ThemeDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/theme/{themeId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId,
                       @RequestBody final ThemeDto resource) {
        service.update(companyId, themeId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/theme")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/theme/{themeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("themeId") final UUID themeId) {
        service.deleteById(companyId, themeId);
    }

}
