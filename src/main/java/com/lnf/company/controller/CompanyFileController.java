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

import com.lnf.company.service.CompanyFileService;
import com.lnf.dto.company.CompanyFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyFileController {

    private final CompanyFileService service;

    @GetMapping(value = "/company/{companyId}/files")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyFileDto> findByCompanyId(@PathVariable UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/files/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> findById(@PathVariable UUID companyId, @PathVariable String fileName) {
        return service.findById(companyId, fileName);
    }

    @PostMapping(value = "/company/{companyId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable UUID companyId, @RequestPart("files") MultipartFile[] files,
                       @RequestPart("resource") List<CompanyFileDto> resource) {
        service.create(companyId, files, resource);
    }

    @PutMapping(value = "/company/{companyId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void update(@PathVariable UUID companyId, @RequestParam String fileName,
                       @RequestPart("file") MultipartFile file, @RequestPart("resource") CompanyFileDto resource) {
        service.update(companyId, fileName, file, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/files")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIdAndFileName(@PathVariable UUID companyId, @RequestParam String fileName) {
        service.deleteByIdAndFileName(companyId, fileName);
    }

}
