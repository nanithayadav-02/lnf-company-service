package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyGstService;
import com.technofacts.lnf.dto.company.GstDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyGstController {

    private final CompanyGstService service;

    @GetMapping(value = "/company/{companyId}/gst")
    public List<GstDto> findByCompanyId(@PathVariable("companyId") final UUID companyId) {
        return service.findByCompanyId(companyId);
    }

    @GetMapping(value = "/company/{companyId}/gst/{gstId}")
    public GstDto findById(@PathVariable("companyId") final UUID companyId, @PathVariable("gstId") final UUID gstId) {
        return service.findById(companyId, gstId);
    }

    @PostMapping(value = "/company/{companyId}/gst")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId, @RequestBody final GstDto resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/gst/{gstId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId, @PathVariable("gstId") final UUID gstId,
                       @RequestBody final GstDto resource) {
        service.update(companyId, gstId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/gst")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/gst/{gstId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId, @PathVariable("gstId") final UUID gstId) {
        service.deleteById(companyId, gstId);
    }
}

