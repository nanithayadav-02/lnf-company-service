package com.lnf.company.controller;

import com.lnf.company.BaseTestClass;
import com.lnf.company.service.CompanyPlanService;
import com.lnf.dto.company.CompanyPlanDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyPlanControllerTest extends BaseTestClass {

    @Autowired
    private CompanyPlanService service;

    @Autowired
    private CompanyPlanController companyPlanController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
    }

    @Test
    void testFindById() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        CompanyPlanDto mockPlan = createMockCompanyPlanDto();

        when(service.findById(companyId, planId)).thenReturn(mockPlan);

        mockMvc.perform(get("/lnf/company/{companyId}/plan/{CompanyPlanId}", companyId, planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    void testFindByCompanyId() throws Exception {
        UUID companyId = UUID.randomUUID();
        List<CompanyPlanDto> mockPlans = Arrays.asList(createMockCompanyPlanDto());

        when(service.findByCompanyId(companyId)).thenReturn(mockPlans);

        mockMvc.perform(get("/lnf/company/{companyId}/plan", companyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Active"));
    }

    @Test
    void testCreate() throws Exception {
        UUID companyId = UUID.randomUUID();
        CompanyPlanDto newPlan = createMockCompanyPlanDto();

        doNothing().when(service).create(companyId, newPlan);

        mockMvc.perform(post("/lnf/company/{companyId}/plan", companyId)
                        .contentType("application/json")
                        .content("{\"planName\":\"New Mock Plan\",\"description\":\"New Mock Plan Description\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        CompanyPlanDto updatedPlan = createMockCompanyPlanDto();

        doNothing().when(service).update(companyId, planId, updatedPlan);

        mockMvc.perform(put("/lnf/company/{companyId}/plan/{CompanyPlanId}", companyId, planId)
                        .contentType("application/json")
                        .content("{\"planName\":\"Updated Mock Plan\",\"description\":\"Updated Mock Plan Description\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteByCompanyId() throws Exception {
        UUID companyId = UUID.randomUUID();

        doNothing().when(service).deleteByCompanyId(companyId);

        mockMvc.perform(delete("/lnf/company/{companyId}/plan", companyId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteById() throws Exception {
        UUID companyId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        doNothing().when(service).deleteById(companyId, planId);

        mockMvc.perform(delete("/lnf/company/{companyId}/plan/{CompanyPlanId}", companyId, planId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testClearCaches() throws Exception {
        doNothing().when(service).clearCaches();

        mockMvc.perform(post("/lnf/company/plan/refresh"))
                .andExpect(status().isCreated());
    }

    public static CompanyPlanDto createMockCompanyPlanDto() {
        CompanyPlanDto companyPlanDto = new CompanyPlanDto();
        companyPlanDto.setId(UUID.randomUUID());
        companyPlanDto.setCompanyId(UUID.randomUUID());
        companyPlanDto.setLnfPlanId(UUID.randomUUID());
        companyPlanDto.setStartDate(LocalDate.of(2023, 1, 1));
        companyPlanDto.setEndDate(LocalDate.of(2023, 12, 31));
        companyPlanDto.setStatus("Active");
        return companyPlanDto;
    }

}
