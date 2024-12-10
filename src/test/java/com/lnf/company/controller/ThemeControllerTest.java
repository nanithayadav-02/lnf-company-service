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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lnf.company.BaseTestClass;
import com.lnf.company.service.ThemeService;
import com.lnf.dto.company.ThemeDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ThemeControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ThemeService service;
    private UUID companyId;

    @BeforeAll
    void beforeAll() {
        companyId = UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5");
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void findByCompanyId() throws Exception {
        List<ThemeDto> expectedDto = List.of(mockTheme1(), mockTheme2());

        given(service.findByCompanyId(any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/theme";

        String resultContent = new String(Files
                .readAllBytes(Path.of(ClassLoader.getSystemResource("testdata/company-theme.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findByCompanyId(any(UUID.class));
    }

    @Test
    void findByCompanyIdAndId() throws Exception {
        UUID id = UUID.fromString("cfe94b9f-c86f-4733-be96-a9b619f7bca7");

        ThemeDto expectedDto = mockTheme1();

        given(service.findById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/theme/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findById(any(UUID.class), any(UUID.class));
    }

    @Test
    void createTheme() {
        // Arrange
        List<ThemeDto> mockTheme = List.of(mockTheme1(), mockTheme2());
        doNothing().when(service).create(companyId, mockTheme);
        String url = "/lnf/company/" + companyId + "/theme";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ThemeDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockTheme)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(service).create(eq(companyId), captor.capture());
        List<ThemeDto> actualThemes = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualThemes.size(), actualThemes.size(), "The number of themes created should match");

        // Check if the details of each event match
        for (int i = 0; i < actualThemes.size(); i++) {
            assertEquals(actualThemes.get(i).getType(), actualThemes.get(i).getType(),
                    "location should match for company at index " + i);
            assertEquals(actualThemes.get(i).getValue(), actualThemes.get(i).getValue(),
                    "number should match for company at index " + i);
        }
    }

    @Test
    void updateTheme() {
        // Arrange
        UUID themeId = UUID.fromString("019d9f96-8f91-4725-9056-ed022b4cb65f");
        ThemeDto updatedTheme = mockTheme2();
        updatedTheme.setId(themeId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId), Mockito.eq(themeId), Mockito.any(ThemeDto.class));
        String url = "/lnf/company/" + companyId + "/theme/" + themeId;
        ArgumentCaptor<ThemeDto> captor = ArgumentCaptor.forClass(ThemeDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedTheme)) // Convert CompanyThemeDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), eq(themeId), captor.capture());
        ThemeDto actualTheme = captor.getValue();

        assertEquals(updatedTheme.getId(), actualTheme.getId(), "Theme IDs should match");
        assertEquals(updatedTheme.getValue(), actualTheme.getValue(), "Theme Value should match");
        assertEquals(updatedTheme.getType(), actualTheme.getType(), "Theme Type should match");
    }

    @Test
    void deleteByCompanyId() throws Exception {
        String url = "/lnf/company/" + companyId + "/theme";

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteByCompanyId(companyId);
    }

    @Test
    void testDeleteByCompanyIdAndId() throws Exception {
        UUID id = UUID.randomUUID();
        String urlTemplate = "/lnf/company/%s/theme/%s".formatted(companyId, id);

        mockMvc.perform(delete(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(companyId, id);
    }

    private ThemeDto mockTheme1() {
        return createTheme("cfe94b9f-c86f-4733-be96-a9b619f7bca7", "SECONDARY", "#f1f1f1");
    }

    private ThemeDto mockTheme2() {
        return createTheme("019d9f96-8f91-4725-9056-ed022b4cb65f", "INFO", "#17a2b8");
    }

    private ThemeDto createTheme(String id, String type, String value) {
        ThemeDto dto = new ThemeDto();
        dto.setId(UUID.fromString(id));
        dto.setType(type);
        dto.setValue(value);

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     ThemeDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.type").value(expectedDto.getType()))
                .andExpect(jsonPath("$.value").value(expectedDto.getValue()));

    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
