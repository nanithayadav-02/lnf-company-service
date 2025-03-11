package com.lnf.company.controller;

import com.lnf.company.service.CompanyLnfPlanService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyLnfPlanDto;
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
public class CompanyLnfPlanController {

    private final CompanyLnfPlanService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/companyLnfPlan")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyLnfPlanDto> findByCompanyId(@PathVariable final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/plan/{companyLnfPlanId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyLnfPlanDto findById(@PathVariable final UUID companyId, @PathVariable final UUID companyLnfPlanId) {
        return service.findById(companyId, companyLnfPlanId);
    }

    @PostMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyId, @RequestBody final CompanyLnfPlanDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/plan/{companyLnfPlanId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyId, @PathVariable final UUID companyLnfPlanId,
                       @RequestBody final CompanyLnfPlanDto resource) {
        service.update(companyId, companyLnfPlanId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/plan")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/plan/{companyLnfPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyId, @PathVariable final UUID companyLnfPlanId) {
        service.deleteById(companyId, companyLnfPlanId);
    }

}
