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

import com.lnf.company.BaseTestClass;
import com.lnf.company.service.CompanyFileService;
import com.lnf.dto.company.CompanyFileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyFileControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CompanyFileService service;

    @InjectMocks
    private CompanyFileController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testFindByCompanyId() throws Exception {
        UUID companyId = UUID.randomUUID();
        List<CompanyFileDto> files = Collections.singletonList(new CompanyFileDto());

        when(service.findByCompanyId(companyId)).thenReturn(files);

        mockMvc.perform(get("/lnf/company/{companyId}/files", companyId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    void testFindById() throws Exception {
        UUID companyId = UUID.randomUUID();
        String fileName = "testfile.txt";
        byte[] fileContent = "file content".getBytes();

        when(service.findById(companyId, fileName)).thenReturn(ResponseEntity.ok(fileContent));

        mockMvc.perform(get("/lnf/company/{companyId}/files/{fileName}", companyId, fileName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @Test
    void testDeleteByIdAndFileName() throws Exception {
        UUID companyId = UUID.randomUUID();
        String fileName = "testfile.txt";

        doNothing().when(service).deleteByIdAndFileName(companyId, fileName);

        mockMvc.perform(delete("/lnf/company/{companyId}/files", companyId)
                        .param("fileName", fileName))
                .andExpect(status().isNoContent());
    }

}
