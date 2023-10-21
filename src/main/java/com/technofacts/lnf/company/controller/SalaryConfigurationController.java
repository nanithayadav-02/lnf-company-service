package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.technofacts.lnf.company.service.SalaryConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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

    @GetMapping(value = "/payroll/salary-configuration/{companyId}")
    public String findByCompanyId(@PathVariable("companyId") final UUID companyId , @RequestParam String financialYear) {
        return salaryConfigurationService.findByCompanyId(companyId,financialYear);
    }

    @PostMapping(value = "/payroll/salary-configuration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@PathVariable("companyId") UUID companyId, @RequestParam String financialYear, @RequestParam MultipartFile file) {
        salaryConfigurationService.create(companyId,financialYear,file);
        return ResponseEntity.ok("payroll salary configuration uploaded successfully");
    }

    @DeleteMapping("/payroll/salary-configuration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void deleteByCompanyId(@PathVariable("companyId") final UUID companyId, @RequestParam String financialYear) {
        salaryConfigurationService.deleteByCompanyId(companyId,financialYear);
    }
}
