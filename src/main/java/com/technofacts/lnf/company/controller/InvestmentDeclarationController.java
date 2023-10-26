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

    private final InvestmentDeclarationService service;

    @GetMapping("/investment-declarations")
    public ResponseEntity<JsonNode> retrieveInvestmentDeclarations() {
        JsonNode desiredJson = service.retrieveInvestmentDeclarations();
        return ResponseEntity.ok(desiredJson);
    }

    @PostMapping(value = "/investment-declaration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@PathVariable("companyId") UUID companyId, @RequestParam String financialYear,
                                         @RequestParam MultipartFile file) {
        service.create(companyId,financialYear,file);
        return ResponseEntity.ok("payroll investment declarations uploaded successfully");
    }


    @DeleteMapping("/investment-declaration/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByCompanyId(@PathVariable("companyId") final UUID companyId, @RequestParam String financialYear) {
        service.deleteByCompanyId(companyId,financialYear);
    }
}
