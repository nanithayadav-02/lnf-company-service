package com.lnf.company.controller;

import com.lnf.company.BaseTestClass;
import com.lnf.company.service.PlanService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.PlanDto;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlanControllerTest extends BaseTestClass {

    @Autowired
    private PlanService service;

    @Autowired
    private PaginationAndSortingHandler paginationAndSortingHandler;

    @Autowired
    private MockMvc mockMvc;

    public PlanControllerTest() {
    }

    @Test
    void findAll() {
        Page<PlanDto> mockedPage = mock(Page.class);
        PageRequestDto pageRequest = new PageRequestDto(0, 10, "id", "asc");

        List<PlanDto> mockedList = List.of(createMockPlanDto());
        when(service.findPaginatedAndSorted(0, 10, "id", "asc")).thenReturn(mockedPage);
        when(service.findPaginated(0, 10)).thenReturn(mockedPage);
        when(service.findAllSorted("id", "asc")).thenReturn(mockedList);
        when(service.findAll()).thenReturn(mockedList);

        PlanController controller = new PlanController(service, paginationAndSortingHandler);
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
    public void testFindById() throws Exception {
        UUID planId = UUID.randomUUID();
        PlanDto mockPlan = new PlanDto();
        mockPlan.setId(planId);
        mockPlan.setPlanName("Test Plan");
        mockPlan.setDescription("Test Plan Description");

        when(service.findById(planId)).thenReturn(mockPlan);

        mockMvc.perform(get("/lnf/company/plans/{planId}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId.toString()))
                .andExpect(jsonPath("$.planName").value("Test Plan"))
                .andExpect(jsonPath("$.description").value("Test Plan Description"));
    }

    @Test
    public void testCreate() throws Exception {
        PlanDto newPlan = new PlanDto();
        newPlan.setPlanName("New Plan");
        newPlan.setDescription("New Plan Description");

        doNothing().when(service).create(newPlan);

        mockMvc.perform(post("/lnf/company/plans")
                        .contentType("application/json")
                        .content("{\"planName\":\"New Plan\",\"description\":\"New Plan Description\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdate() throws Exception {
        UUID planId = UUID.randomUUID();
        PlanDto updatedPlan = new PlanDto();
        updatedPlan.setPlanName("Updated Plan");
        updatedPlan.setDescription("Updated Plan Description");

        doNothing().when(service).update(planId, updatedPlan);

        mockMvc.perform(put("/lnf/company/plans/{planId}", planId)
                        .contentType("application/json")
                        .content("{\"planName\":\"Updated Plan\",\"description\":\"Updated Plan Description\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        UUID planId = UUID.randomUUID();

        doNothing().when(service).deleteById(planId);

        mockMvc.perform(delete("/lnf/company/plans/{planId}", planId))
                .andExpect(status().isNoContent());
    }

    public static PlanDto createMockPlanDto() {
        PlanDto planDto = new PlanDto();
        planDto.setId(UUID.randomUUID());
        planDto.setPlanName("Mock Plan Name");
        planDto.setDescription("This is a mock description for the plan.");
        return planDto;
    }

}
