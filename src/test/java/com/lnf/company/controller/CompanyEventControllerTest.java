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
import com.lnf.company.service.CompanyEventService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyEventDto;
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
import java.nio.file.Path;
import java.time.LocalDate;
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

class CompanyEventControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyEventService service;
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
        Page<CompanyEventDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "eventType", "asc");

        List<CompanyEventDto> mockedList = List.of(mockEvent1(), mockEvent2());
        when(service.findPaginatedAndSorted(0, 10, "eventType", "asc")).thenReturn(mockedPage);
        when(service.findPaginated(0, 10)).thenReturn(mockedPage);
        when(service.findAllSorted("eventType", "asc")).thenReturn(mockedList);
        when(service.findAll()).thenReturn(mockedList);

        CompanyEventController controller = new CompanyEventController(service, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(pageRequest, false);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10, null, null);
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with sortBy and sortOrder
        pageRequest = new PageRequestDto(null, null, "eventType", "asc");
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);

        //find All
        pageRequest = new PageRequestDto();
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);
    }

    @Test
    void findByCompanyId() throws Exception {
        List<CompanyEventDto> expectedDto = Arrays.asList(mockEvent1(), mockEvent2());

        given(service.findByCompanyId(any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/event";

        String resultContent = new String(Files
                .readAllBytes(Path.of(ClassLoader.getSystemResource("testdata/company-events.json")
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

        CompanyEventDto expectedDto = mockEvent1();

        given(service.findById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/event/" + id;

        performAndVerifyGet(url, status().isOk(), id.toString(), expectedDto);

        verify(service, times(1)).findById(any(UUID.class), any(UUID.class));
    }

    @Test
    void createCompanyEvents() {
        // Arrange
        List<CompanyEventDto> mockEvent = List.of(mockEvent1(), mockEvent2());
        doNothing().when(service).create(companyId, mockEvent);
        String url = "/lnf/company/" + companyId + "/events";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CompanyEventDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockEvent)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(service).create(eq(companyId), captor.capture());
        List<CompanyEventDto> actualEvents = captor.getValue();

        // Check if the lists have the same size
        assertEquals(actualEvents.size(), actualEvents.size(), "The number of events created should match");

        // Check if the details of each event match
        for (int i = 0; i < actualEvents.size(); i++) {
            assertEquals(actualEvents.get(i).getEventDescription(), actualEvents.get(i).getEventDescription(),
                    "EventDescription should match for company at index " + i);
            assertEquals(actualEvents.get(i).getEventType(), actualEvents.get(i).getEventType(),
                    "EventType should match for company at index " + i);
            assertEquals(actualEvents.get(i).getStatus(), actualEvents.get(i).getStatus(),
                    "Status should match for company at index " + i);
            assertEquals(actualEvents.get(i).getAssignTo(), actualEvents.get(i).getAssignTo(),
                    "AssignCode should match for company at index " + i);
            assertEquals(actualEvents.get(i).getDateAndTime(), actualEvents.get(i).getDateAndTime(),
                    "DateAndTime should match for company at index " + i);
        }
    }

    @Test
    void testCreateCompanyEvent() {
        CompanyEventDto requestDto = mockEvent1();

        String url = "/lnf/company/" + companyId + "/event";

        doNothing().when(service).create(eq(companyId), any(CompanyEventDto.class));

        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(requestDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        verify(service, times(1)).create(eq(companyId), any(CompanyEventDto.class));
    }

    @Test
    void updateCompanyEvent() {
        // Arrange
        UUID eventId = UUID.fromString("019d9f96-8f91-4725-9056-ed022b4cb65f");
        CompanyEventDto updatedEvent = mockEvent2();
        updatedEvent.setId(eventId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId), Mockito.eq(eventId), Mockito.any(CompanyEventDto.class));
        String url = "/lnf/company/" + companyId + "/event/" + eventId;
        ArgumentCaptor<CompanyEventDto> captor = ArgumentCaptor.forClass(CompanyEventDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedEvent)) // Convert CompanyEventDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), eq(eventId), captor.capture());
        CompanyEventDto actualEvent = captor.getValue();

        assertEquals(updatedEvent.getId(), actualEvent.getId(), "Event IDs should match");
        assertEquals(updatedEvent.getDateAndTime(), actualEvent.getDateAndTime(), "Event DateAndTime should match");
        assertEquals(updatedEvent.getEventDescription(), actualEvent.getEventDescription(), "Event EventDescription should match");
        assertEquals(updatedEvent.getAssignTo(), actualEvent.getAssignTo(), "Event AssignTo should match");
        assertEquals(updatedEvent.getStatus(), actualEvent.getStatus(), "Event Status should match");
        assertEquals(updatedEvent.getEventType(), actualEvent.getEventType(), "Event EventType should match");

    }

    @Test
    void deleteByCompanyId() throws Exception {

        String url = "/lnf/company/" + companyId + "/events";

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteByCompanyId(companyId);
    }

    @Test
    void deleteByCompanyIdAndId() throws Exception {
        UUID id = UUID.randomUUID();
        String urlTemplate = "/lnf/company/%s/event/%s".formatted(companyId, id);

        mockMvc.perform(delete(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).deleteById(companyId, id);
    }

    private CompanyEventDto mockEvent1() {
        return createEvent("cfe94b9f-c86f-4733-be96-a9b619f7bca7", "Birthday", "2024-04-30", "Vikranth Thakur",
                "31st birthday", "InProgress");
    }

    private CompanyEventDto mockEvent2() {
        return createEvent("019d9f96-8f91-4725-9056-ed022b4cb65f", "Release", "2024-04-02", "Pritham k Shende",
                "Product is Launching", "Release");
    }

    private CompanyEventDto createEvent(String id, String eventType, String dateAndTime, String assignTo, String eventDescription, String status) {
        CompanyEventDto dto = new CompanyEventDto();
        dto.setId(UUID.fromString(id));
        dto.setEventType(eventType);
        dto.setDateAndTime(LocalDate.parse((dateAndTime)));
        dto.setAssignTo(assignTo);
        dto.setEventDescription(eventDescription);
        dto.setStatus(status);

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     CompanyEventDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.eventType").value(expectedDto.getEventType()))
                .andExpect(jsonPath("$.dateAndTime").value(expectedDto.getDateAndTime().toString()))
                .andExpect(jsonPath("$.assignTo").value(expectedDto.getAssignTo()))
                .andExpect(jsonPath("$.eventDescription").value(expectedDto.getEventDescription()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus()));

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
