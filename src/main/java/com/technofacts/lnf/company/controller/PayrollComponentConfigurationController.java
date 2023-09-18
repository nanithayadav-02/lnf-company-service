package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.PayrollComponentConfigurationService;
import com.technofacts.lnf.dto.company.PayrollComponentConfigurationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class PayrollComponentConfigurationController {

    private final PayrollComponentConfigurationService payrollComponentConfigurationService;

    @GetMapping(value = "/payrollComponentConfigurations/{companyId}")
    public List<PayrollComponentConfigurationDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return payrollComponentConfigurationService.findByCompanyId(companyId);
    }

    @GetMapping(value = "/payrollComponentConfigurations")
    public List<PayrollComponentConfigurationDto> findAll() {
        return payrollComponentConfigurationService.findAll();
    }

    @PostMapping(value = "/payrollComponentConfiguration/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,@RequestBody final  PayrollComponentConfigurationDto resource) {
        payrollComponentConfigurationService.create(companyId,resource);
    }

    @PostMapping(value = "/payrollComponentConfigurations/{companyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final List<PayrollComponentConfigurationDto> resources) {
        payrollComponentConfigurationService.create(companyId, resources);
    }

    @PutMapping(value = "/{companyId}/payrollComponentConfiguration/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("id") final UUID id, @RequestBody final PayrollComponentConfigurationDto resource) {
        payrollComponentConfigurationService.update(companyId, id, resource);
    }

    @DeleteMapping(value = "/{companyId}/payrollComponentConfiguration/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId,@PathVariable("id") final UUID id) {
        payrollComponentConfigurationService.deleteByCompanyIdAndId(companyId,id);
    }

    @DeleteMapping(value = "/{companyId}/payrollComponentConfiguration")
    public void deleteByCompanyId(@PathVariable("companyId") final UUID companyId) {
        payrollComponentConfigurationService.deleteByCompanyId(companyId);
    }
}
