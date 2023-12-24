package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyEventService;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import com.technofacts.lnf.util.QueryConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf/company")
public class CompanyEventController {

    private final CompanyEventService companyEventService;

    @GetMapping(value = "/event", params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT_BY})
    public Page<CompanyEventDto> findAllPaginatedAndSorted(@RequestParam(value = QueryConstants.PAGE) final int page,
                                                           @RequestParam(value = QueryConstants.SIZE) final int size,
                                                           @RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                                           @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return companyEventService.findPaginatedAndSorted(page, size, sortBy, sortOrder);
    }

    @GetMapping(value = "/event", params = {QueryConstants.PAGE, QueryConstants.SIZE})
    public Page<CompanyEventDto> findAllPaginated(@RequestParam(value = QueryConstants.PAGE) final int page,
                                                  @RequestParam(value = QueryConstants.SIZE) final int size) {
        return companyEventService.findPaginated(page, size);
    }

    @GetMapping(value = "/event", params = {QueryConstants.SORT_BY})
    public List<CompanyEventDto> findAllSorted(@RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                               @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return companyEventService.findAllSorted(sortBy, sortOrder);
    }

    @GetMapping(value = "/events")
    public List<CompanyEventDto> findAll() {
        return companyEventService.findAll();
    }

    @GetMapping(value = "/{companyId}/event")
    public List<CompanyEventDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return companyEventService.findByCompanyId(companyId);
    }

    @GetMapping(value = "/{companyId}/event/{eventId}")
    public CompanyEventDto findById(@PathVariable("companyId") final UUID companyId,
                                    @PathVariable("eventId") final UUID eventId) {
        return companyEventService.findById(companyId, eventId);
    }

    @PostMapping(value = "/{companyId}/event")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final CompanyEventDto resource) {
        companyEventService.create(companyId, resource);
    }

    @PostMapping(value = "/{companyId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final List<CompanyEventDto> resource) {
        companyEventService.create(companyId, resource);
    }

    @PutMapping(value = "/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("eventId") final UUID eventId,
                       @RequestBody final CompanyEventDto resource) {
        companyEventService.update(companyId, eventId, resource);
    }


    @DeleteMapping(value = "/{companyId}/events")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        companyEventService.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/{companyId}/event/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("eventId") final UUID eventId) {
        companyEventService.deleteById(companyId, eventId);
    }
}
