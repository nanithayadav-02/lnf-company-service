package com.technofacts.lnf.company.service;

import com.google.common.collect.Lists;
import com.technofacts.lnf.company.converter.CompanyEventConverter;
import com.technofacts.lnf.company.exception.LnFEntityNotFoundException;
import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.repository.CompanyEventRepository;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import com.technofacts.lnf.service.common.page.PaginatedAndSortedService;
import com.technofacts.lnf.util.RestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyEventSchedulerService implements PaginatedAndSortedService<CompanyEventDto> {

    private final CompanyEventRepository companyEventRepository;

    @Override
    public Page<CompanyEventDto> findPaginatedAndSorted(int page, int size, String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size, sortInfo));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyEventDto> findAll() {
        List<CompanyEvent> entities = companyEventRepository.findAll();
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    @Override
    public Page<CompanyEventDto> findPaginated(int page, int size) {
        Page<CompanyEvent> resultPage = companyEventRepository.findAll(PageRequest.of(page, size));
        return validateAndGetPages(page, resultPage);
    }

    @Override
    public List<CompanyEventDto> findAllSorted(String sortBy, String sortOrder) {
        final Sort sortInfo = RestUtil.constructSort(sortBy, sortOrder);
        List<CompanyEvent> entities = Lists.newArrayList(companyEventRepository.findAll(sortInfo));
        return entities.stream().map(CompanyEventConverter::toTransportModel).filter(Objects::nonNull).toList();
    }

    private Page<CompanyEventDto> validateAndGetPages(int page, Page<CompanyEvent> resultPage) {
        if (page > resultPage.getTotalPages()) {
            throw new LnFEntityNotFoundException(String.format("Total number of pages [%d], " + "requested page [%d] does not exist", resultPage.getTotalPages(), page));
        }
        return resultPage.map(CompanyEventConverter::toTransportModel);
    }

    public List<CompanyEventDto> findEventsByTypeAndDate(EventType eventType, LocalDate dateAndTime) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByDate(eventType, dateAndTime);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByCurrentDate() {
        List<CompanyEvent> entities = companyEventRepository.findEventsByCurrentDate();
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByEventTypeAndDateRange(EventType eventType, LocalDate startDate, LocalDate endDate) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByEventTypeAndDateRange(eventType, startDate, endDate);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByWeek(EventType eventType, int week) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByWeek(eventType, week);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByMonth(EventType eventType, int month) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByMonth(eventType, month);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByYear(EventType eventType, int year) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByYear(eventType, year);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

    public List<CompanyEventDto> findEventsByMonthAndYear(EventType eventType, int month, int year) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByMonthAndYear(eventType, month, year);
        return entities.stream().map(CompanyEventConverter::toTransportModel).toList();
    }

}
