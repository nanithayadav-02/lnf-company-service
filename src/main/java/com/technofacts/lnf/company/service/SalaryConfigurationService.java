package com.technofacts.lnf.company.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.technofacts.lnf.company.exception.LnFException;
import lombok.extern.java.Log;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Log
public class SalaryConfigurationService {

    public JsonNode retrieveSalaryConfigurations() {
        try {
            Resource resource = new ClassPathResource("SalaryConfigurations.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(resource.getInputStream());
            JsonNode salaryComponents = jsonData.get("salary_components");
            JsonNode salaryDeductions = jsonData.get("salary_deductions");
            ObjectMapper newObjectMapper = new ObjectMapper();
            ObjectNode desiredJson = newObjectMapper.createObjectNode();
            desiredJson.set("salary_components", salaryComponents);
            desiredJson.set("salary_deductions", salaryDeductions);

            return desiredJson;
        } catch (Exception e) {
            throw new LnFException("Failed to retrieve salary configurations", e);
        }
    }
}
