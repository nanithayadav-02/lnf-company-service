package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyHolidayService;
import com.technofacts.lnf.dto.company.AddressDto;
import com.technofacts.lnf.dto.company.CompanyHolidayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyHolidayController {

    private final CompanyHolidayService service;

    @GetMapping("/company/holidays")
    public List<CompanyHolidayDto> holidaysList(){
        return service.getAllHolidays();
    }

    @GetMapping("/company/{holidayId}/holiday")
    public CompanyHolidayDto holiday(@PathVariable("holidayId") final UUID holidayId){
        return service.getHoliday(holidayId);
    }

    @GetMapping("/company/{companyId}/holidays/pdf")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> downloadAsPdf(@PathVariable("companyId") final UUID companyId) {
        return service.downloadHolidaysAsPdf(companyId);
    }

    @PostMapping(value = "/company/{companyId}/holiday")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable("companyId") final UUID companyId,
                       @RequestBody final List<CompanyHolidayDto> resource) {
        service.create(companyId, resource);
    }

    @PutMapping(value = "/company/{companyId}/holiday/{holidayId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("holidayId") final UUID holidayId, @RequestBody final CompanyHolidayDto resource) {
        service.update(companyId, holidayId, resource);
    }

    @DeleteMapping(value = "/company/{companyId}/holiday")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId) {
        service.deleteByCompanyId(companyId);
    }

    @DeleteMapping(value = "/company/{companyId}/holiday/{holidayId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("companyId") final UUID companyId,
                       @PathVariable("holidayId") final UUID holidayId) {
        service.deleteById(companyId, holidayId);
    }


}
