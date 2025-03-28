package com.lnf.company.controller;

import com.lnf.company.service.CompanyPlanAuditService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyPlanAuditDto;
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
public class CompanyPlanAuditController {

    private final CompanyPlanAuditService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/plan/history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{CompanyPlanId}/plan/history")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyPlanAuditDto> findByCompanyPlanId(@PathVariable final UUID CompanyPlanId) {
        return service.findByCompanyPlanId(CompanyPlanId);
    }

    @GetMapping(value = "/company/{CompanyPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyPlanAuditDto findById(@PathVariable final UUID CompanyPlanId, @PathVariable final UUID historyId) {
        return service.findById(CompanyPlanId, historyId);
    }

    @PostMapping(value = "/company/{CompanyPlanId}/plan/history")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable final UUID CompanyPlanId, @RequestBody final CompanyPlanAuditDto resource) {
        service.create(CompanyPlanId, resource);
    }

    @PutMapping(value = "/company/{CompanyPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID CompanyPlanId, @PathVariable final UUID historyId,
                       @RequestBody final CompanyPlanAuditDto resource) {
        service.update(CompanyPlanId, historyId, resource);
    }

    @DeleteMapping(value = "/company/{CompanyPlanId}/plan/history")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID CompanyPlanId) {
        service.deleteByCompanyPlanId(CompanyPlanId);
    }

    @DeleteMapping(value = "/company/{CompanyPlanId}/plan/history/{historyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID CompanyPlanId, @PathVariable final UUID historyId) {
        service.deleteById(CompanyPlanId, historyId);
    }

    @GetMapping(value = "/company/{CompanyId}/history")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyPlanAuditDto> findByCompanyId(@PathVariable final UUID CompanyId) {
        return service.findByCompanyId(CompanyId);
    }

}
