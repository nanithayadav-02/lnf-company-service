package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyEventService;
import com.technofacts.lnf.dto.common.PageRequestDto;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import com.technofacts.lnf.service.common.page.PageableAsQueryParam;
import com.technofacts.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyEventController {

    private final CompanyEventService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/company/event")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping(value = "/company/{companyId}/event")
    public List<CompanyEventDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/event/{eventId}")
    public CompanyEventDto findById(@PathVariable("companyId") final UUID companyId,
                                    @PathVariable("eventId") final UUID eventId) {
        return service.findById(companyId, eventId);
    }

    @PostMapping(value = "/company/{companyId}/event")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final CompanyEventDto resource) {
        service.create(companyId, resource);
    }

    @PostMapping(value = "/company/{companyId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final List<CompanyEventDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("eventId") final UUID eventId,
                       @RequestBody final CompanyEventDto resource) {
        service.update(companyId, eventId, resource);
    }


    @DeleteMapping(value = "/company/{companyId}/events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("eventId") final UUID eventId) {
        service.deleteById(companyId, eventId);
    }

}
