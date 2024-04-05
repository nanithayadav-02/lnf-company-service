package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technofacts.lnf.company.BaseTestClass;
import com.technofacts.lnf.company.service.CompanyService;
import com.technofacts.lnf.dto.common.PageRequestDto;
import com.technofacts.lnf.dto.company.CompanyDto;
import com.technofacts.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CompanyControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyService service;
    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;
    private String companyCode;
    private UUID companyId;

    @BeforeAll
    void beforeAll() {
       companyCode = "TF";
       companyId = UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5");
    }

    @BeforeEach
    void setUp() {
        // Common setup code if necessary
    }

    @Test
    void findAll() {
        Page<CompanyDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "name", "asc");

        List<CompanyDto> mockedList = List.of(createCompany());
        when(service.findPaginatedAndSorted(0, 10, "name", "asc")).thenReturn(mockedPage);
        when(service.findPaginated(0, 10)).thenReturn(mockedPage);
        when(service.findAllSorted("name", "asc")).thenReturn(mockedList);
        when(service.findAll()).thenReturn(mockedList);

        CompanyController controller = new CompanyController(service, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(pageRequest);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10,null,null);
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with sortBy and sortOrder
        pageRequest = new PageRequestDto(null, null, "name", "asc");
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);

        //find All
        pageRequest = new PageRequestDto();
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);
    }

    @Test
    void findCompanyByCode() throws Exception {
        CompanyDto expectedDto = mockCompany1();

        given(service.findByCompanyCode(any(String.class))).willReturn(expectedDto);

        String url = "/lnf/company/" + companyCode;

        performAndVerifyGet(url, status().isOk(),companyCode, expectedDto);

        verify(service, times(1)).findByCompanyCode(any(String.class));
    }

    @Test
    void search() throws Exception {
        String businessCategory = "IT";

        List<CompanyDto> expectedDTOs = List.of(mockCompany1());

        given(service.findAll(businessCategory)).willReturn(expectedDTOs);

        String url = "/lnf/company?search=" + businessCategory;

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expectedDTOs.size()));

        verify(service, times(1)).findAll(businessCategory);
    }

    @Test
    void create() {
        CompanyDto requestDto = mockCompany1();

        String url = "/lnf/company";

        doNothing().when(service).create(any(CompanyDto.class));

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(requestDto)))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }

        verify(service, times(1)).create(any(CompanyDto.class));

    }

    @Test
    void updateCompany() {
        CompanyDto updatedCompany = mockCompany1();
        updatedCompany.setId(companyId);

        Mockito.doNothing().when(service).update(Mockito.eq(companyId), Mockito.any(CompanyDto.class));
        String url = "/lnf/company/" + companyId;
        ArgumentCaptor<CompanyDto> captor = ArgumentCaptor.forClass(CompanyDto.class);

        // Act
        try {
            mockMvc.perform(put(url)
                            .content(asJsonString(updatedCompany)) // Convert CompanyDto to JSON string
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }

        //verify the service
        verify(service).update(eq(companyId), any(CompanyDto.class));

        // Assert
        Mockito.verify(service, times(1)).update(eq(companyId), captor.capture());
        CompanyDto actualType = captor.getValue();

        assertEquals(updatedCompany.getId(), actualType.getId(), "Company IDs should match");
        assertEquals(updatedCompany.getCode(), actualType.getCode(), "Company code should match");
        assertEquals(updatedCompany.getName(), actualType.getName(), "Company name should match");
        assertEquals(updatedCompany.getStatus(), actualType.getStatus(), "Company status should match");
        assertEquals(updatedCompany.getEmail(), actualType.getEmail(), "Company email should match");
        assertEquals(updatedCompany.getTelephone(), actualType.getTelephone(), "Company telephone should match");
        assertEquals(updatedCompany.getMobile(), actualType.getMobile(), "Company mobile should match");
        assertEquals(updatedCompany.getWebsite(), actualType.getWebsite(), "Company website should match");
        assertEquals(updatedCompany.getBusinessCategory(), actualType.getBusinessCategory(), "Company business category should match");
        assertEquals(updatedCompany.getBusinessDescription(), actualType.getBusinessDescription(), "Company business description should match");
        assertEquals(updatedCompany.getPan(), actualType.getPan(), "Company PAN should match");
        assertEquals(updatedCompany.getArn(), actualType.getArn(), "Company ARN should match");
        assertEquals(updatedCompany.getArnIssueDate(), actualType.getArnIssueDate(), "Company ARN issue date should match");
        assertEquals(updatedCompany.getSacCode(), actualType.getSacCode(), "Company SAC code should match");
    }

    @Test
    void deleteCompany() throws Exception {
        String url = "/lnf/company/" + companyId;
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service).delete(companyId);
    }

    private CompanyDto mockCompany1() {
        return createCompany();
    }

    private CompanyDto createCompany() {
        CompanyDto dto = new CompanyDto();
        dto.setId(UUID.fromString("eaae6ae2-f6da-4e4d-9ad9-8808b98965c5"));
        dto.setCode("TF");
        dto.setName("Technofacts solutions Ltd");
        dto.setStatus("active");
        dto.setEmail("ajith@technofacts.co.uk");
        dto.setTelephone("07898789899");
        dto.setMobile("9123446789");
        dto.setWebsite("www.techno-facts.com");
        dto.setBusinessCategory("IT");
        dto.setBusinessDescription("IT Consulting");
        dto.setPan("YUGFJ2046S");
        dto.setArn("123456789");
        dto.setArnIssueDate(LocalDate.parse("2021-01-01"));
        dto.setSacCode(Long.valueOf("123456789"));

        return dto;
    }

    private void performAndVerifyGet(String url, ResultMatcher statusMatcher, String containsString,
                                     CompanyDto expectedDto) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(statusMatcher)
                .andExpect(content().string(containsString(containsString))) // Validate the ID
                .andExpect(jsonPath("$.id").value(expectedDto.getId().toString())) // Additional, more specific validation
                .andExpect(jsonPath("$.code").value(expectedDto.getCode()))
                .andExpect(jsonPath("$.name").value(expectedDto.getName()))
                .andExpect(jsonPath("$.status").value(expectedDto.getStatus()))
                .andExpect(jsonPath("$.email").value(expectedDto.getEmail()))
                .andExpect(jsonPath("$.telephone").value(expectedDto.getTelephone()))
                .andExpect(jsonPath("$.mobile").value(expectedDto.getMobile()))
                .andExpect(jsonPath("$.website").value(expectedDto.getWebsite()))
                .andExpect(jsonPath("$.businessCategory").value(expectedDto.getBusinessCategory()))
                .andExpect(jsonPath("$.businessDescription").value(expectedDto.getBusinessDescription()))
                .andExpect(jsonPath("$.pan").value(expectedDto.getPan()))
                .andExpect(jsonPath("$.arn").value(expectedDto.getArn()))
                .andExpect(jsonPath("$.arnIssueDate").value(expectedDto.getArnIssueDate().toString()))
                .andExpect(jsonPath("$.sacCode").value(expectedDto.getSacCode()));
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
