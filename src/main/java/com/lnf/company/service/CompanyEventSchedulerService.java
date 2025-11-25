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

package com.lnf.company.service;

import com.lnf.company.converter.CompanyEventConverter;
import com.lnf.company.model.CompanyEvent;
import com.lnf.company.model.enums.EventType;
import com.lnf.company.repository.CompanyEventRepository;
import com.lnf.company.utils.CompanyUtil;
import com.lnf.dto.company.CompanyEventDto;
import com.lnf.exception.LnFEntityNotFoundException;
import com.lnf.service.common.page.PaginatedAndSortedService;
import com.lnf.util.RestUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompanyEventSchedulerService implements PaginatedAndSortedService<CompanyEventDto> {

    private final CompanyEventRepository companyEventRepository;
    private final CompanyUtil companyUtil;

    private BiFunction<EventType, Integer, List<CompanyEvent>> eventsByWeek;
    private BiFunction<EventType, Integer, List<CompanyEvent>> eventsByMonth;
    private BiFunction<EventType, Integer, List<CompanyEvent>> eventsByYear;

    @PostConstruct
    public void init() {
        eventsByWeek = companyEventRepository::findEventsByWeek;
        eventsByMonth = companyEventRepository::findEventsByMonth;
        eventsByYear = companyEventRepository::findEventsByYear;
    }

    @Override
    public List<CompanyEventDto> findAll() {
        return convertAndFilter(companyEventRepository.findAll());
    }

    @Override
    public Page<CompanyEventDto> findPaginated(int page, int size) {
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public Page<CompanyEventDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyEventDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        return convertAndFilter(companyEventRepository.findAll(sortInfo));
    }

    public List<CompanyEventDto> findEventsByTypeAndDate(EventType eventType, LocalDate dateAndTime) {
        return fetchAndTransform(() -> companyEventRepository.findEventsByDate(eventType, dateAndTime));
    }

    public List<CompanyEventDto> findEventsByCurrentDate(EventType eventType, boolean myEvents) {
        if (eventType == null) {
            String email = myEvents ? companyUtil.getEmail() : null;
            return fetchAndTransform(() -> companyEventRepository.findEventsByCurrentDate(email));
        } else {
            return fetchAndTransform(() -> companyEventRepository.findEventsByCurrentDate(eventType));
        }
    }

    public List<CompanyEventDto> findEventsByEventTypeAndDateRange(EventType eventType, LocalDate startDate,
                                                                   LocalDate endDate) {
        return fetchAndTransform(() ->
                companyEventRepository.findEventsByEventTypeAndDateRange(eventType, startDate, endDate));
    }

    public List<CompanyEventDto> findEventsByMonthAndYear(EventType eventType, int month, int year) {
        return fetchAndTransform(() -> companyEventRepository.findEventsByMonthAndYear(eventType, month, year));
    }

    public List<CompanyEventDto> findEventsByWeek(EventType eventType, Integer week) {
        return findEvents(eventType, week, eventsByWeek);
    }

    public List<CompanyEventDto> findEventsByMonth(EventType eventType, Integer month) {
        return findEvents(eventType, month, eventsByMonth);
    }

    public List<CompanyEventDto> findEventsByYear(EventType eventType, Integer year) {
        return findEvents(eventType, year, eventsByYear);
    }

    private Page<CompanyEventDto> validateAndGetPages(int page, Page<CompanyEvent> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(prepareErrorMessage(resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyEventConverter::toTransportModel);
    }

    private String prepareErrorMessage(int totalPages, int requestedPage) {
        return "Total number of pages [%d], requested page [%d] does not exist".formatted(
                totalPages, requestedPage);
    }

    private List<CompanyEventDto> convertAndFilter(List<CompanyEvent> entities) {
        return entities.stream()
                .map(CompanyEventConverter::toTransportModel)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<CompanyEventDto> fetchAndTransform(Supplier<List<CompanyEvent>> fetcher) {
        List<CompanyEvent> entities = fetcher.get();
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    private List<CompanyEventDto> findEvents(EventType eventType, Integer timeOffset,
                                             BiFunction<EventType, Integer, List<CompanyEvent>> repositoryFunc) {
        List<CompanyEvent> entities;
        if (timeOffset != null) {
            entities = repositoryFunc.apply(eventType, timeOffset);
        } else if (eventType == null) {
            entities = companyEventRepository.findAll();
        } else {
            entities = companyEventRepository.findEventsByType(eventType);
        }
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

}
