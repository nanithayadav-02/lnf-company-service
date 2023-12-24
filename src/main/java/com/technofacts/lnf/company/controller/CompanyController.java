package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyService;
import com.technofacts.lnf.dto.company.CompanyDto;
import com.technofacts.lnf.util.QueryConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyController {

    private final CompanyService service;

    @GetMapping(value = "/company", params = {QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT_BY})
    public List<CompanyDto> findAllPaginatedAndSorted(@RequestParam(value = QueryConstants.PAGE) final int page,
                                                      @RequestParam(value = QueryConstants.SIZE) final int size,
                                                      @RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                                      @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return service.findPaginatedAndSorted(page, size, sortBy, sortOrder);
    }

    @GetMapping(value = "/company", params = {QueryConstants.PAGE, QueryConstants.SIZE})
    public List<CompanyDto> findAllPaginated(@RequestParam(value = QueryConstants.PAGE) final int page,
                                             @RequestParam(value = QueryConstants.SIZE) final int size) {
        return service.findPaginated(page, size);
    }

    @GetMapping(value = "/company", params = {QueryConstants.SORT_BY})
    public List<CompanyDto> findAllSorted(@RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
                                          @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return service.findAllSorted(sortBy, sortOrder);
    }

    @GetMapping(value = "/company")
    public List<CompanyDto> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/company", params = {"search"})
    public List<CompanyDto> search(@RequestParam(value = "search") String search) {
        return service.findAll(search);
    }


    @GetMapping(value = "/company/{companyCode}")
    public CompanyDto findCompany(@PathVariable("companyCode") final String companyCode) {
        return service.findByCompanyCode(companyCode);
    }

    @PostMapping(value = "/company")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final CompanyDto resource) {
        service.create(resource);
    }

    @PutMapping(value = "/company/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @RequestBody final CompanyDto resource) {
        service.update(companyId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.delete(companyId);
    }
}
