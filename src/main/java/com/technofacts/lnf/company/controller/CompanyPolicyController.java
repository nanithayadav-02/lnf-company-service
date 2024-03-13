package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyPolicyService;
import com.technofacts.lnf.dto.company.CompanyPolicyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyPolicyController {

    private final CompanyPolicyService service;

    @GetMapping(value = "/company/{companyId}/policies")
    public List<CompanyPolicyDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/policies/{fileName}")
    public ResponseEntity<byte[]> findById(@PathVariable("companyId") final UUID companyId, @RequestParam(value = "policyId", required = false) final UUID policyId,
                                           @PathVariable("fileName") String fileName) {
        return service.findById(companyId, policyId, fileName);
    }

    @PostMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestParam MultipartFile[] policies) {
        service.create(companyId, policies);
    }

    @PutMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @RequestParam(value = "policyId", required = false) final UUID policyId,
                       @RequestParam MultipartFile policy) {
        service.update(companyId, policyId, policy);
    }

    @DeleteMapping(value = "/company/{companyId}/policies")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/policies/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @RequestParam(value = "policyId", required = false) final UUID policyId,
                       @PathVariable("fileName") final String fileName) {
        service.deleteById(companyId, policyId, fileName);
    }

}
