/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.controller;

import com.lnf.company.model.enums.EventType;
import com.lnf.company.service.CompanyEventSchedulerService;
import com.lnf.dto.common.PageRequestDto;
import com.lnf.dto.company.CompanyEventDto;
import com.lnf.service.common.page.PageableAsQueryParam;
import com.lnf.service.common.page.PaginationAndSortingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lnf")
public class CompanyEventSchedulerController {

    private final CompanyEventSchedulerService service;
    private final PaginationAndSortingHandler paginationAndSortingHandler;

    @GetMapping("/company/events")
    public ResponseEntity<?> findAll(@PageableAsQueryParam PageRequestDto pageRequest) {
        return paginationAndSortingHandler.handleFindAllRequest(pageRequest, service);
    }

    @GetMapping("/company/events/{eventType}/date/{date}")
    public List<CompanyEventDto> findEventsByDate(@PathVariable EventType eventType, @PathVariable LocalDate date) {
        return service.findEventsByTypeAndDate(eventType, date);
    }

    @GetMapping({"/company/events/{eventType}/currentDate", "/company/events/currentDate"})
    public List<CompanyEventDto> findEventsByCurrentDate(@PathVariable(required = false) EventType eventType,
                                                         @RequestParam(required = false) boolean myEvents) {
        return service.findEventsByCurrentDate(eventType, myEvents);
    }

    @GetMapping("/company/events/{eventType}/range/{startDate}/{endDate}")
    public List<CompanyEventDto> findEventsByEventTypeAndDateRange(
            @PathVariable EventType eventType, @PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        return service.findEventsByEventTypeAndDateRange(eventType, startDate, endDate);
    }

    @GetMapping("/company/events/{eventType}/week/{week}")
    public List<CompanyEventDto> findEventsByWeek(
            @PathVariable(required = false) EventType eventType,
            @PathVariable(required = false) Integer week) {
        return service.findEventsByWeek(eventType, week);
    }

    @GetMapping("/company/events/{eventType}/month/{month}")
    public List<CompanyEventDto> findEventsByMonth(
            @PathVariable(required = false) EventType eventType,
            @PathVariable(required = false) Integer month) {
        return service.findEventsByMonth(eventType, month);
    }

    @GetMapping("/company/events/{eventType}/year/{year}")
    public List<CompanyEventDto> findEventsByYear(
            @PathVariable(required = false) EventType eventType,
            @PathVariable(required = false) Integer year) {
        return service.findEventsByYear(eventType, year);
    }

    @GetMapping("/company/events/{eventType}/monthYear/{month}/{year}")
    public List<CompanyEventDto> findEventsByMonthAndYear(
            @PathVariable EventType eventType, @PathVariable int month, @PathVariable int year) {
        return service.findEventsByMonthAndYear(eventType, month, year);
    }

}
