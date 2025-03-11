package com.lnf.company.controller;

import com.lnf.company.service.CompanyLnfPlanAuditService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyLnfPlanAuditDto;
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
public class CompanyLnfPlanAuditController {

    private final CompanyLnfPlanAuditService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/plan/history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyLnfPlanId}/plan/history")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyLnfPlanAuditDto> findByCompanyLnfPlanId(@PathVariable final UUID companyLnfPlanId) {
        return service.findByCompanyLnfPlanId(companyLnfPlanId);
    }

    @GetMapping(value = "/company/{companyLnfPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyLnfPlanAuditDto findById(@PathVariable final UUID companyLnfPlanId, @PathVariable final UUID historyId) {
        return service.findById(companyLnfPlanId, historyId);
    }

    @PostMapping(value = "/company/{companyLnfPlanId}/plan/history")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID companyLnfPlanId, @RequestBody final CompanyLnfPlanAuditDto resource) {
        service.create(companyLnfPlanId, resource);
    }

    @PutMapping(value = "/company/{companyLnfPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID companyLnfPlanId, @PathVariable final UUID historyId,
                       @RequestBody final CompanyLnfPlanAuditDto resource) {
        service.update(companyLnfPlanId, historyId, resource);
    }

    @DeleteMapping(value = "/company/{companyLnfPlanId}/plan/history")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyLnfPlanId) {
        service.deleteByCompanyLnfPlanId(companyLnfPlanId);
    }

    @DeleteMapping(value = "/company/{companyLnfPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID companyLnfPlanId, @PathVariable final UUID historyId) {
        service.deleteById(companyLnfPlanId, historyId);
    }

}
