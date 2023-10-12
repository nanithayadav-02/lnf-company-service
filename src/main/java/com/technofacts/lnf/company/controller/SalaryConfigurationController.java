package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.technofacts.lnf.company.service.SalaryConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class SalaryConfigurationController {

    private final SalaryConfigurationService salaryConfigurationService;

    @GetMapping("/salary-configurations")
    public ResponseEntity<JsonNode> getSalaryConfigurations() {

            JsonNode desiredJson = salaryConfigurationService.retrieveSalaryConfigurations();
            return ResponseEntity.ok(desiredJson);
        }

}
