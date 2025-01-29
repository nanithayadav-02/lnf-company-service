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
import com.lnf.company.service.CompanyHolidayService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyHolidayDto;
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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
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

class CompanyHolidayControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyHolidayService holidayService;
    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;

    private UUID companyId;

    @BeforeAll
    void beforeAll() {
        companyId = UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5");
    }

    @BeforeEach
    void setUp() {
        // Common setup code if necessary
    }

    @Test
    void findHoliday_WhenValidInput_ReturnsHolidayDto() throws Exception {
        UUID holidayId = UUID.fromString("1bb2af60-5565-41f8-b60a-6d139cba5178");
        CompanyHolidayDto expectedDto = mockHoliday1();

        given(holidayService.findHolidayById(any(UUID.class), any(UUID.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyId + "/holidays/" + holidayId;

        performAndVerifyGet(url, status().isOk(), holidayId.toString(), expectedDto);
        verify(holidayService, times(1)).findHolidayById(any(UUID.class), any(UUID.class));
    }

    @Test
    void findAllWithPagination() {
        String search = "location:Bangalore";
        Page<CompanyHolidayDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "description", "asc");

        List<CompanyHolidayDto> mockedList = List.of(mockHoliday1(), mockHoliday2(), mockHoliday3());

        when(holidayService.findPaginatedAndSorted(0, 10, "description", "asc")).thenReturn(mockedPage);
        when(holidayService.findPaginated(0, 10)).thenReturn(mockedPage);
        when(holidayService.findAllSorted("description", "asc")).thenReturn(mockedList);
        when(holidayService.findAll()).thenReturn(mockedList);
        when(holidayService.findingAllWithPagination(search, pageRequest)).thenReturn(mockedPage);

        CompanyHolidayController controller = new CompanyHolidayController(holidayService, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(search, pageRequest);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10, null, null);
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, holidayService);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with sortBy and sortOrder
        pageRequest = new PageRequestDto(null, null, "description", "asc");
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, holidayService);
        assertEquals(ResponseEntity.ok(mockedList), response);

        //find All
        pageRequest = new PageRequestDto();
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, holidayService);
        assertEquals(ResponseEntity.ok(mockedList), response);
    }

    @Test
    void testFindHolidaysByYearAndLocation() throws Exception {
        given(holidayService.findHolidaysByYearAndLocation(any(UUID.class), anyLong(), anyString()))
                .willReturn(Arrays.asList(mockHoliday1(), mockHoliday2()));

        // When & Then
        mockMvc.perform(get("/lnf/company/%s/holidays/%d/%s".formatted(companyId, 2024, "Hyderabad")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Year")))
                .andExpect(content().string(containsString("Independence Day")));

        verify(holidayService, times(1))
                .findHolidaysByYearAndLocation(any(UUID.class), anyLong(), anyString());
    }

    @Test
    void shouldReturnPdfWhenCompanyHolidaysAreRequested() throws Exception {
        List<CompanyHolidayDto> mockHolidays = Arrays.asList(mockHoliday1(),
                mockHoliday2(), mockHoliday3());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(mockHolidays);
        byte[] holidayDtoListPdfContent = baos.toByteArray();

        when(holidayService.getCompanyHolidaysAsPdf(companyId)).thenReturn(holidayDtoListPdfContent);

        String url = "/lnf/company/" + companyId + "/holidays/pdf";

        mockMvc.perform(get(url).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=company-holidays.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(holidayDtoListPdfContent));

        verify(holidayService, times(1)).getCompanyHolidaysAsPdf(any(UUID.class));
    }

    @Test
    void testCreateHolidays() {
        // Arrange
        List<CompanyHolidayDto> mockHolidays = List.of(mockHoliday4());
        doNothing().when(holidayService).create(companyId, mockHolidays);
        String url = "/lnf/company/" + companyId + "/holidays";

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CompanyHolidayDto>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        try {
            mockMvc.perform(post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(mockHolidays)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        verify(holidayService).create(eq(companyId), captor.capture());
        List<CompanyHolidayDto> actualHolidays = captor.getValue();

        // Check if the lists have the same size
        assertEquals(mockHolidays.size(), actualHolidays.size(), "The number of holidays created should match");

        // Check if the details of each holiday match
        for (int i = 0; i < mockHolidays.size(); i++) {
            assertEquals(mockHolidays.get(i).getDescription(), actualHolidays.get(i).getDescription(),
                    "Descriptions should match for holiday at index " + i);
            assertEquals(mockHolidays.get(i).getDate(), actualHolidays.get(i).getDate(),
                    "Date should match for holiday at index " + i);
            assertEquals(mockHolidays.get(i).getLocation(), actualHolidays.get(i).getLocation(),
                    "Location should match for holiday at index " + i);
        }
    }

    @Test
    void updateHolidayTest() {
        // Arrange
        UUID holidayId = UUID.randomUUID();
        CompanyHolidayDto updatedHoliday = mockHoliday4();
        updatedHoliday.setId(holidayId);

        Mockito.doNothing().when(holidayService).update(Mockito.eq(companyId), Mockito.eq(holidayId), Mockito.any(CompanyHolidayDto.class));
        String url = "/lnf/company/" + companyId + "/holidays/" + holidayId;
        ArgumentCaptor<CompanyHolidayDto> captor = ArgumentCaptor.forClass(CompanyHolidayDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedHoliday)) // Convert CompanyHolidayDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        Mockito.verify(holidayService, times(1)).update(eq(companyId), eq(holidayId), captor.capture());
        CompanyHolidayDto actualHoliday = captor.getValue();

        assertEquals(updatedHoliday.getId(), actualHoliday.getId(), "Holiday IDs should match");
        assertEquals(updatedHoliday.getDescription(), actualHoliday.getDescription(), "Holiday descriptions should match");
        assertEquals(updatedHoliday.getDate(), actualHoliday.getDate(), "Holiday date should match");
        assertEquals(updatedHoliday.getLocation(), actualHoliday.getLocation(), "Holiday location should match");
    }

    @Test
    void deleteAllHolidaysByCompanyId_WhenCalled_ShouldCallService() throws Exception {
        String url = "/lnf/company/" + companyId + "/holidays";
        mockMvc.perform(delete(url, companyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(holidayService).deleteAll(companyId);
    }

    @Test
    void shouldDeleteHolidayByIdAndCompanyId() throws Exception {
        UUID holidayId = UUID.randomUUID();

        String urlTemplate = "/lnf/company/%s/holidays/%s".formatted(companyId, holidayId);

        mockMvc.perform(delete(urlTemplate))
                .andExpect(status().isNoContent());

        verify(holidayService).deleteById(companyId, holidayId);
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     CompanyHolidayDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(containsString)) // Additional, more specific validation
                .andExpect(jsonPath("$.description").value(expectedDto.getDescription()))
                .andExpect(jsonPath("$.location").value(expectedDto.getLocation()));
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

    private CompanyHolidayDto mockHoliday1() {
        return createHoliday("1bb2af60-5565-41f8-b60a-6d139cba5178", "2024-01-01", "New Year", "Hyderabad");
    }

    private CompanyHolidayDto mockHoliday2() {
        return createHoliday("63c85f6b-8c71-4f46-b9ff-278eba7dad44", "2024-08-15", "Independence Day", "Hyderabad");
    }

    private CompanyHolidayDto mockHoliday3() {
        return createHoliday("fc24295f-c28f-4046-b614-8f9dc4665d62", "2024-01-26", "Republic Day", "Bangalore");
    }

    private CompanyHolidayDto mockHoliday4() {
        return createHoliday("cc24295f-c28f-4046-b614-8f9dc4665d62", "2024-12-25", "Christmas Day", "Hyderabad");
    }

    private CompanyHolidayDto createHoliday(String id, String date, String description, String location) {
        CompanyHolidayDto dto = new CompanyHolidayDto();
        dto.setId(UUID.fromString(id));
        dto.setDate(LocalDate.parse(date));
        dto.setDescription(description);
        dto.setDeleted(false);
        dto.setLocation(location);
        return dto;
    }

}
