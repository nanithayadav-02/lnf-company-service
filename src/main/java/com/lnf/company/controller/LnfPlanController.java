package com.lnf.company.controller;

import com.lnf.company.service.LnfPlanService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.LnfPlanDto;
import com.lnf.service.common.page.PageableAsQueryParam;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class LnfPlanController {

    private final LnfPlanService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/plans")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/plans/{planId}")
    @ResponseStatus(HttpStatus.OK)
    public LnfPlanDto findById(@PathVariable final UUID planId) {
        return service.findById(planId);
    }

    @PostMapping(value = "/company/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final LnfPlanDto resource) {
        service.create(resource);
    }

    @PutMapping(value = "/company/plans/{planId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable final UUID planId,
                       @RequestBody final LnfPlanDto resource) {
        service.update(planId, resource);
    }

    @DeleteMapping(value = "/company/plans/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID planId) {
        service.deleteById(planId);
    }

}
