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
import com.lnf.company.model.enums.EventType;
import com.lnf.company.service.CompanyEventSchedulerService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyEventDto;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyEventSchedulerControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyEventSchedulerService service;

    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;

    @BeforeAll
    void beforeAll() {
       //TobeImplemented
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

        CompanyEventSchedulerController controller = new CompanyEventSchedulerController(service, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(pageRequest);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10,null,null);
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
    void findEventsByDate() throws Exception {

        EventType eventType = EventType.Birthday;
        LocalDate date = LocalDate.of(2024, 4, 2);
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/date/{date}";

        given(service.findEventsByTypeAndDate(eventType, date)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, eventType, date))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByTypeAndDate(eventType, date);

    }

    @Test
    void findEventsByCurrentDate() throws Exception {

        String url = "/lnf/company/events/currentDate";
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        given(service.findEventsByCurrentDate(null)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByCurrentDate(null);
    }

    @Test
    void findEventsByEventTypeAndDateRange () throws Exception {
        EventType eventType = EventType.Birthday;
        LocalDate startDate = LocalDate.of(2024, 3, 25);
        LocalDate endDate = LocalDate.of(2024, 4, 2);
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/range/{startDate}/{endDate}";

        given(service.findEventsByEventTypeAndDateRange(eventType, startDate, endDate)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, String.valueOf(eventType), startDate.toString(), endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByEventTypeAndDateRange(eventType, startDate, endDate);
    }

    @Test
    void findEventsByWeek () throws Exception {
        EventType eventType = EventType.Birthday;
        int week = 1;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/week/{week}";

        given(service.findEventsByWeek(eventType, week)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, String.valueOf(eventType), String.valueOf(week)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByWeek(eventType, week);
    }

    @Test
    void findEventsByMonth () throws Exception {
        EventType eventType = EventType.Birthday;
        int month = 2;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/month/{month}";

        given(service.findEventsByMonth(eventType, month)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, String.valueOf(eventType), String.valueOf(month)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByMonth(eventType, month);
    }

    @Test
    void findEventsByYear () throws Exception {
        EventType eventType = EventType.Birthday;
        int year = 2024;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/year/{year}";

        given(service.findEventsByYear(eventType, year)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, String.valueOf(eventType), String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByYear(eventType, year);
    }

    @Test
    void findEventsByMonthAndYear () throws Exception {
        EventType eventType = EventType.Birthday;
        int month = 2;
        int year = 2024;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/company/events/{eventType}/monthYear/{month}/{year}";

        given(service.findEventsByMonthAndYear(eventType, month, year)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url, String.valueOf(eventType), String.valueOf(month),  String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByMonthAndYear(eventType, month, year);
    }

    private CompanyEventDto mockEvent1() {
        return  createEvent("019d9f96-8f91-4725-9056-ed022b4cb65f", "Pritham k Shende",
                "25th birthday");
    }

    private CompanyEventDto mockEvent2() {
        return  createEvent("9060225c-f356-45b4-a89a-ef91ea859e60", "Kushbu Sharma",
                "28th birthday");
    }

    private CompanyEventDto createEvent(String id, String assignTo, String eventDescription) {
        CompanyEventDto dto = new CompanyEventDto ();
        dto.setId(UUID.fromString(id));
        dto.setEventType("Birthday");
        dto.setDateAndTime(LocalDate.parse(("2024-04-02")));
        dto.setAssignTo(assignTo);
        dto.setEventDescription(eventDescription);
        dto.setStatus("ACTIVE");

        return dto;
    }

}
