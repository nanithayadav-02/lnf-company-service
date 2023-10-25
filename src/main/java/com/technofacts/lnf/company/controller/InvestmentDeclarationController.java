package com.technofacts.lnf.company.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.technofacts.lnf.company.service.InvestmentDeclarationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class InvestmentDeclarationController {

    private final InvestmentDeclarationService investmentDeclarationService;

    @GetMapping("/investment-declarations")
    public ResponseEntity<JsonNode> retrieveInvestmentDeclarations() {
        JsonNode desiredJson = investmentDeclarationService.retrieveInvestmentDeclarations();
        return ResponseEntity.ok(desiredJson);
    }


    @PostMapping(value = "/payroll/investment-declaration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@PathVariable("companyId") UUID companyId, @RequestParam String financialYear, @RequestParam MultipartFile file) {
        investmentDeclarationService.create(companyId,financialYear,file);
        return ResponseEntity.ok("payroll investment declaration uploaded successfully");
    }


    @DeleteMapping("/payroll/investment-declaration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void deleteByCompanyId(@PathVariable("companyId") final UUID companyId, @RequestParam String financialYear) {
        investmentDeclarationService.deleteByCompanyId(companyId,financialYear);
    }
}
