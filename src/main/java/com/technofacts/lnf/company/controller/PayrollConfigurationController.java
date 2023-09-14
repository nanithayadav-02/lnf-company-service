package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.PayrollConfigurationService;
import com.technofacts.lnf.dto.company.PayrollConfigurationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class PayrollConfigurationController {

    private final PayrollConfigurationService payrollConfigurationService;

    @GetMapping(value = "/{companyId}/payrollConfiguration")
    public PayrollConfigurationDto findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return payrollConfigurationService.findByCompanyId(companyId);
    }

    @PostMapping(value = "/{companyId}/payrollConfiguration")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,@RequestBody final  PayrollConfigurationDto resource) {
        payrollConfigurationService.create(companyId,resource);
    }

    @PutMapping(value = "/{companyId}/payrollConfiguration/{payrollConfigurationId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("payrollConfigurationId") final UUID payrollConfigurationId, @RequestBody final PayrollConfigurationDto resource) {
        payrollConfigurationService.update(companyId, payrollConfigurationId, resource);
    }

    @DeleteMapping(value = "/{companyId}/payrollConfiguration")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        payrollConfigurationService.deleteByCompanyId(companyId);
    }
}
