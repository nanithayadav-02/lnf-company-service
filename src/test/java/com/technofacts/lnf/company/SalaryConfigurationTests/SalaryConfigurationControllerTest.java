package com.technofacts.lnf.company.SalaryConfigurationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(MockitoExtension.class)
public class SalaryConfigurationControllerTest {

    @Mock
    private SalaryConfigurationService salaryConfigurationService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SalaryConfigurationController(salaryConfigurationService)).build();

        JsonNode jsonData = createMockJsonData();
        Mockito.when(salaryConfigurationService.retrieveSalaryConfigurations()).thenReturn(jsonData);
    }

    @Test
    public void testGetSalaryConfigurations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/lnf/company/salary-configurations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private JsonNode createMockJsonData() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mockJsonData = objectMapper.createObjectNode();
        ObjectNode salaryComponents = objectMapper.createObjectNode();
        ObjectNode salaryDeductions = objectMapper.createObjectNode();

        salaryComponents.put("basic_salary", " Basic Salary Data");
        salaryComponents.put("hra", " HRA Data");

        salaryDeductions.put("employee_provident_fund", " Employee PF Data");
        salaryDeductions.put("health_insurance", " Health Insurance Data");


        mockJsonData.set("salary_components", salaryComponents);
        mockJsonData.set("salary_deductions", salaryDeductions);

        return mockJsonData;
    }
}
