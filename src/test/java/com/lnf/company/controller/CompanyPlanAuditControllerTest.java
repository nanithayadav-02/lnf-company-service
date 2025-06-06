package com.lnf.company.controller;

import com.lnf.company.BaseTestClass;
import com.lnf.company.service.CompanyPlanAuditService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyPlanAuditDto;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyPlanAuditControllerTest extends BaseTestClass {

    @Autowired
    private CompanyPlanAuditService service;

    @Autowired
    private CompanyPlanAuditController companyPlanAuditController;

    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
    }

    @Test
    void findAll() {
        Page<CompanyPlanAuditDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", "asc");

        List<CompanyPlanAuditDto> mockedList = List.of(createMockCompanyPlanAuditDto());
        when(service.findPaginatedAndSorted(0, 10, "id", "asc")).thenReturn(mockedPage);
        when(service.findPaginated(0, 10)).thenReturn(mockedPage);
        when(service.findAllSorted("id", "asc")).thenReturn(mockedList);
        when(service.findAll()).thenReturn(mockedList);

        CompanyPlanAuditController controller = new CompanyPlanAuditController(service, paginationAndSortingHandler);
        // Test for paginated and sorted request
        ResponseEntity<?> response = controller.findAll(pageRequest);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with  sortBy and sortOrder
        pageRequest = new PageRequestDto(0, 10, null, null);
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedPage), response);

        // Pagination with sortBy and sortOrder
        pageRequest = new PageRequestDto(null, null, "id", "asc");
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);

        //find All
        pageRequest = new PageRequestDto();
        response = paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
        assertEquals(ResponseEntity.ok(mockedList), response);
    }

    @Test
    void testFindByCompanyPlanId() throws Exception {
        UUID companyPlanId = UUID.randomUUID();
        List<CompanyPlanAuditDto> mockAuditList = Arrays.asList(createMockCompanyPlanAuditDto());

        when(service.findByCompanyPlanId(companyPlanId)).thenReturn(mockAuditList);

        mockMvc.perform(get("/lnf/company/{CompanyPlanId}/plan/history", companyPlanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Active"));
    }

    @Test
    void testFindById() throws Exception {
        UUID companyPlanId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        CompanyPlanAuditDto mockAudit = createMockCompanyPlanAuditDto();

        when(service.findById(companyPlanId, historyId)).thenReturn(mockAudit);

        mockMvc.perform(get("/lnf/company/{CompanyPlanId}/plan/history/{historyId}", companyPlanId, historyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void testCreate() throws Exception {
        UUID companyPlanId = UUID.randomUUID();
        CompanyPlanAuditDto newAudit = createMockCompanyPlanAuditDto();

        doNothing().when(service).create(companyPlanId, newAudit);

        mockMvc.perform(post("/lnf/company/{CompanyPlanId}/plan/history", companyPlanId)
                        .contentType("application/json")
                        .content("{\"planName\":\"New Mock Plan\",\"description\":\"New Mock Plan Description\",\"status\":\"Active\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        UUID companyPlanId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();
        CompanyPlanAuditDto updatedAudit = createMockCompanyPlanAuditDto();

        doNothing().when(service).update(companyPlanId, historyId, updatedAudit);

        mockMvc.perform(put("/lnf/company/{CompanyPlanId}/plan/history/{historyId}", companyPlanId, historyId)
                        .contentType("application/json")
                        .content("{\"planName\":\"Updated Mock Plan\",\"description\":\"Updated Mock Plan Description\",\"status\":\"Active\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteByCompanyPlanId() throws Exception {
        UUID companyPlanId = UUID.randomUUID();

        doNothing().when(service).deleteByCompanyPlanId(companyPlanId);

        mockMvc.perform(delete("/lnf/company/{CompanyPlanId}/plan/history", companyPlanId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteByHistoryId() throws Exception {
        UUID companyPlanId = UUID.randomUUID();
        UUID historyId = UUID.randomUUID();

        doNothing().when(service).deleteById(companyPlanId, historyId);

        mockMvc.perform(delete("/lnf/company/{CompanyPlanId}/plan/history/{historyId}", companyPlanId, historyId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindByCompanyId() throws Exception {
        UUID companyId = UUID.randomUUID();
        List<CompanyPlanAuditDto> mockAuditList = Arrays.asList(createMockCompanyPlanAuditDto());

        when(service.findByCompanyId(companyId)).thenReturn(mockAuditList);

        mockMvc.perform(get("/lnf/company/{CompanyId}/history", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Active"));
    }

    private static CompanyPlanAuditDto createMockCompanyPlanAuditDto() {
        CompanyPlanAuditDto companyPlanAuditDto = new CompanyPlanAuditDto();
        companyPlanAuditDto.setId(UUID.randomUUID());
        companyPlanAuditDto.setCompanyPlanId(UUID.randomUUID());
        companyPlanAuditDto.setStartDate(LocalDate.of(2023, 1, 1));
        companyPlanAuditDto.setEndDate(LocalDate.of(2023, 12, 31));
        companyPlanAuditDto.setStatus("Active"); // Example status
        companyPlanAuditDto.setPlanName("Mock Plan Name");
        companyPlanAuditDto.setDescription("Mock Plan Description");
        return companyPlanAuditDto;
    }

}

