package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technofacts.lnf.company.config.SalaryComponentConfiguration;
import com.technofacts.lnf.company.model.SalaryComponent;
import com.technofacts.lnf.company.model.SalaryDeduction;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class SalaryConfigurationController {

    @GetMapping("/salary-configurations")
    public ResponseEntity<SalaryComponentConfiguration> getSalaryConfigurations() {
        try {

            Resource resource = new ClassPathResource("SalaryConfigurations.json");


            ObjectMapper objectMapper = new ObjectMapper();
            SalaryComponentConfiguration salaryComponents = objectMapper.readValue(resource.getInputStream(), SalaryComponentConfiguration.class);


            Map<String, SalaryComponent> salaryComponentMap = salaryComponents.getSalaryComponents();
            Map<String, SalaryDeduction> salaryDeductionMap = salaryComponents.getSalaryDeductions();

            return ResponseEntity.ok(salaryComponents);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
