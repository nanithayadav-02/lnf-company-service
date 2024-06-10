package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.service.CompanyEventSchedulerService;
import com.technofacts.lnf.dto.common.PageRequestDto;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import com.technofacts.lnf.service.common.page.PageableAsQueryParam;
import com.technofacts.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyEventSchedulerController {

    private final CompanyEventSchedulerService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping(value = "/companyEvents")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping("/companyEvent/eventType/date")
    public List<CompanyEventDto> findEventsByDate(@RequestParam("eventType") EventType eventType
            , @RequestParam("date") LocalDate date) {
        return service.findEventsByTypeAndDate(eventType, date);
    }

    @GetMapping("/companyEvent/eventType/currentDate")
    public List<CompanyEventDto> findEventsByCurrentDate() {
        return service.findEventsByCurrentDate();
    }

    @GetMapping("/companyEvent/eventType/startDate/endDate")
    public List<CompanyEventDto> findEventsByEventTypeAndDateRange(@RequestParam("eventType") EventType eventType
            , @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return service.findEventsByEventTypeAndDateRange(eventType, startDate, endDate);
    }

    @GetMapping("/companyEvent/eventType/week")
    public List<CompanyEventDto> findEventsByWeek(@RequestParam(value = "eventType",required = false) EventType eventType
            , @RequestParam(value = "week",required = false) Integer week) {
        return service.findEventsByWeek(eventType, week);
    }

    @GetMapping("/companyEvent/eventType/month")
    public List<CompanyEventDto> findEventsByMonth(@RequestParam(value = "eventType",required = false) EventType eventType
            , @RequestParam(value = "month",required = false) Integer month) {
        return service.findEventsByMonth(eventType, month);
    }

    @GetMapping("/companyEvent/eventType/year")
    public List<CompanyEventDto> findEventsByYear(@RequestParam(value = "eventType",required = false) EventType eventType
            , @RequestParam(value = "year",required = false) Integer year) {
        return service.findEventsByYear(eventType, year);
    }

    @GetMapping("/companyEvent/eventType/month/year")
    public List<CompanyEventDto> findEventsByMonthAndYear(@RequestParam("eventType") EventType eventType
            , @RequestParam("month") int month, @RequestParam("year") int year) {
        return service.findEventsByMonthAndYear(eventType, month, year);
    }

}
