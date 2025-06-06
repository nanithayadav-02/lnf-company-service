package com.lnf.company.controller;

import com.lnf.company.service.CompanyPlanService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyPlanDto;
import com.lnf.service.common.page.PageableAsQueryParam;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyPlanController {

    private final CompanyPlanService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/CompanyPlan")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyPlanDto> findByCompanyId(@PathVariable final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/plan/{companyPlanId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyPlanDto findById(@PathVariable final UUID companyId, @PathVariable final UUID companyPlanId) {
        return service.findById(companyId, companyPlanId);
    }

    @PostMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId, @RequestBody final CompanyPlanDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/plan/{companyPlanId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyId, @PathVariable final UUID companyPlanId,
                       @RequestBody final CompanyPlanDto resource) {
        service.update(companyId, companyPlanId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/plan/{companyPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId, @PathVariable final UUID companyPlanId) {
        service.deleteById(companyId, companyPlanId);
    }

    @PostMapping("/company/plan/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public void clearCaches() {
        service.clearCaches();
    }

}
