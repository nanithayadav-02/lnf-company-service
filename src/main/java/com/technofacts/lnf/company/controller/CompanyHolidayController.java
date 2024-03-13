package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.service.CompanyHolidayService;
import com.technofacts.lnf.dto.company.CompanyHolidayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyHolidayController {

    private static final String COMPANY_HOLIDAYS_PDF_FILE = "company-holidays.pdf";
    private final CompanyHolidayService holidayService;

    @GetMapping("/company/{companyId}/holidays/{holidayId}")
    @ResponseStatus(HttpStatus.OK)
    public CompanyHolidayDto findHoliday(@PathVariable("companyId") final UUID companyId,
                                        @PathVariable("holidayId") final UUID holidayId) {
        return holidayService.findHolidayById(companyId, holidayId);
    }

    @GetMapping("/company/holidays")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyHolidayDto> findAllHolidays() {
        return holidayService.findAll();
    }

    @GetMapping("/company/{companyId}/holidays/{year}/{location}")
    @ResponseStatus(HttpStatus.OK)
    public List<CompanyHolidayDto> findHolidaysByYearAndLocation(@PathVariable("companyId") final UUID companyId,
                                                                 @PathVariable("year") final long year,
                                                                 @PathVariable("location") final String location) {
        return holidayService.findHolidaysByYearAndLocation(companyId, year, location);
    }

    @GetMapping("/company/{companyId}/holidays/pdf")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getCompanyHolidaysAsPdf(@PathVariable("companyId") final UUID companyId) {
        byte[] pdfContent = holidayService.getCompanyHolidaysAsPdf(companyId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ COMPANY_HOLIDAYS_PDF_FILE)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @PostMapping("/company/{companyId}/holidays")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHolidays(@PathVariable("companyId") final UUID companyId,
                               @RequestBody final List<CompanyHolidayDto> holidays) {
        holidayService.create(companyId, holidays);
    }

    @PutMapping("/company/{companyId}/holidays/{holidayId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateHoliday(@PathVariable("companyId") final UUID companyId,
                              @PathVariable("holidayId") final UUID holidayId,
                              @RequestBody final CompanyHolidayDto updatedHoliday) {
        holidayService.update(companyId, holidayId, updatedHoliday);
    }

    @DeleteMapping("/company/{companyId}/holidays")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllHolidaysByCompanyId(@PathVariable("companyId") final UUID companyId) {
        holidayService.deleteAll(companyId);
    }

    @DeleteMapping("/company/{companyId}/holidays/{holidayId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHolidayByIdAndCompanyId(@PathVariable("companyId") final UUID companyId,
                                              @PathVariable("holidayId") final UUID holidayId) {
        holidayService.deleteById(companyId, holidayId);
    }

}
