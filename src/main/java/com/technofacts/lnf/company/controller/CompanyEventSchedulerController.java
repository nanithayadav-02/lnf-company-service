package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.service.CompanyEventSchedulerService;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import lombok.RequiredArgsConstructor;
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

    private final CompanyEventSchedulerService companyEventSchedulerService;

    @GetMapping("/companyEvent/eventType/date")
    public List<CompanyEventDto> findEventsByDate(@RequestParam("eventType") EventType eventType, @RequestParam("date") LocalDate date) {
        return companyEventSchedulerService.findEventsByTypeAndDate(eventType, date);
    }

    @GetMapping("/companyEvent/eventType/startDate/endDate")
    public List<CompanyEventDto> findEventsByEventTypeAndDateRange(@RequestParam("eventType") EventType eventType, @RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        return companyEventSchedulerService.findEventsByEventTypeAndDateRange(eventType, startDate, endDate);
    }

    @GetMapping("/companyEvent/eventType/week")
    public List<CompanyEventDto> findEventsByWeek(@RequestParam("eventType") EventType eventType, @RequestParam("week") int week) {
        return companyEventSchedulerService.findEventsByWeek(eventType, week);
    }

    @GetMapping("/companyEvent/eventType/month")
    public List<CompanyEventDto> findEventsByMonth(@RequestParam("eventType") EventType eventType, @RequestParam("month") int month) {
        return companyEventSchedulerService.findEventsByMonth(eventType, month);
    }

    @GetMapping("/companyEvent/eventType/year")
    public List<CompanyEventDto> findEventsByYear(@RequestParam("eventType") EventType eventType, @RequestParam("year") int year) {
        return companyEventSchedulerService.findEventsByYear(eventType, year);
    }

    @GetMapping("/companyEvent/eventType/month/year")
    public List<CompanyEventDto> findEventsByMonthAndYear(@RequestParam("eventType") EventType eventType, @RequestParam("month") int month, @RequestParam("year") int year) {
        return companyEventSchedulerService.findEventsByMonthAndYear(eventType, month, year);
    }

}
