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
import com.lnf.company.service.CompanyNotesService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.NotesDto;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

class CompanyNotesControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyNotesService service;
    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;
    private UUID companyId;

    @BeforeAll
    void beforeAll() {
        companyId = UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5");
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAll() {
        Page<NotesDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "notes", "asc");

        List<NotesDto> mockedList = List.of(mockNotes1(), mockNotes1());
        when(service.findPaginatedAndSorted(0, 10, "notes", "asc")).thenReturn(mockedPage);
        when(service.findPaginated(0, 10)).thenReturn(mockedPage);
        when(service.findAllSorted("notes", "asc")).thenReturn(mockedList);
        when(service.findAll()).thenReturn(mockedList);

        CompanyNotesController controller = new CompanyNotesController(service, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(pageRequest);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10,null,null);
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with sortBy and sortOrder
        pageRequest = new PageRequestDto(null, null, "notes", "asc");
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);

        //find All
        pageRequest = new PageRequestDto();
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);
    }

    @Test
    void findByCompanyId() throws Exception {
        List<NotesDto> expectedDto = Arrays.asList(mockNotes1(), mockNotes2());

        given(service.findByCompanyId (any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/notes";

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-notes.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findByCompanyId (any(UUID.class));
    }

    @Test
    void findByCompanyIdAndId() throws Exception {
        UUID id = UUID.fromString("cfe94b9f-c86f-4733-be96-a9b619f7bca7");

        NotesDto expectedDto = mockNotes1();

        given(service.findById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/notes/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findById(any(UUID.class), any(UUID.class));
    }

    @Test
    void createCompanyNotes() {
        // Arrange
        List<NotesDto> mockNotes = List.of (mockNotes1 (), mockNotes2 ());
        doNothing ().when (service).create (companyId, mockNotes);
        String url = "/lnf/company/" + companyId + "/notes";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<NotesDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform (post (url)
                            .contentType (APPLICATION_JSON)
                            .content (asJsonString(mockNotes)))
                    .andExpect (status ().isCreated());
        } catch (Exception e) {
            fail ("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify (service).create (eq(companyId), captor.capture());
        List<NotesDto> actualNotes = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualNotes.size(), actualNotes.size(), "The number of notes created should match");

        // Check if the details of each event match
        for (int i = 0; i < actualNotes.size (); i++) {
            assertEquals (actualNotes.get(i).getNotes(), actualNotes.get(i).getNotes (),
                    "Notes should match for company at index " + i);
        }
    }

    @Test
    void createNotes() {
        NotesDto requestDto = mockNotes1();

        String url = "/lnf/company/" + companyId + "/note";

        doNothing().when(service).create(eq(companyId), any(NotesDto.class));

        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(requestDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        verify(service, times(1)).create(eq(companyId), any(NotesDto.class));
    }

    @Test
    void update () {
        // Arrange
        UUID notesId = UUID.fromString ("cfe94b9f-c86f-4733-be96-a9b619f7bca7");
        NotesDto updatedNotes = mockNotes1 ();
        updatedNotes.setId(notesId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId), Mockito.eq(notesId), Mockito.any(NotesDto.class));
        String url = "/lnf/company/" + companyId + "/notes/" + notesId;
        ArgumentCaptor<NotesDto> captor = ArgumentCaptor.forClass(NotesDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedNotes)) // Convert CompanyNotesDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), eq(notesId), captor.capture());
        NotesDto actualNotes = captor.getValue();

        assertEquals(updatedNotes.getId(), actualNotes.getId(), "Notes IDs should match");
        assertEquals(updatedNotes.getNotes (), actualNotes.getNotes (), "notes  should match");
    }

    @Test
    void deleteByCompanyId() throws Exception {
        String url = "/lnf/company/" + companyId + "/notes";

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteByCompanyId (companyId);
    }

    @Test
    void deleteByCompanyIdAndId() throws Exception {
        UUID id = UUID.randomUUID();
        String urlTemplate = String.format("/lnf/company/%s/notes/%s", companyId, id);

        mockMvc.perform(delete(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(companyId, id);
    }

    private NotesDto mockNotes1() {
        return createNotes("cfe94b9f-c86f-4733-be96-a9b619f7bca7", "the bug has to fix");
    }

    private NotesDto mockNotes2() {
        return createNotes("019d9f96-8f91-4725-9056-ed022b4cb65f", "the product is launching");
    }

    private NotesDto createNotes(String id, String notes) {
        NotesDto dto = new NotesDto ();
        dto.setId(UUID.fromString(id));
        dto.setNotes (notes);

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     NotesDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.notes").value(expectedDto.getNotes()));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper ()
                    .registerModule(new JavaTimeModule ())
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
