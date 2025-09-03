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

import com.lnf.company.service.CompanyPolicyService;
import com.lnf.dto.company.CompanyPolicyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyPolicyController {

    private final CompanyPolicyService service;

    @GetMapping(value = "/company/{companyId}/policies")
    public List<CompanyPolicyDto> findByCompanyId(@PathVariable final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/policies/{fileName}")
    public ResponseEntity<byte[]> findById(@PathVariable final UUID companyId, @PathVariable String fileName) {
        return service.findById(companyId, fileName);
    }

    @PostMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId, @RequestParam MultipartFile[] policies) {
        service.create(companyId, policies);
    }

    @PutMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyId, @RequestParam MultipartFile policy) {
        service.update(companyId, policy);
    }

    @DeleteMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId, @RequestParam final String fileName) {
        service.deleteById(companyId, fileName);
    }

}
