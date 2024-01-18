package com.technofacts.lnf.company.SalaryConfigurationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

public class SalaryConfigurationServiceTest {

    @InjectMocks
    private SalaryConfigurationService salaryConfigurationService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveSalaryConfigurations() throws Exception {
        Resource resource = new ClassPathResource("SalaryConfigurations.json");
        JsonNode jsonData = new ObjectMapper().readTree(resource.getInputStream());
        when(objectMapper.readTree(resource.getInputStream())).thenReturn(jsonData);
        JsonNode result = salaryConfigurationService.retrieveSalaryConfigurations();
        assertEquals(jsonData.get("salary_components"), result.get("salary_components"));
        assertEquals(jsonData.get("salary_deductions"), result.get("salary_deductions"));
    }
}
