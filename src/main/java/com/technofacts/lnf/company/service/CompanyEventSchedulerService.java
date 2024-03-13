package com.technofacts.lnf.company.service;

import com.technofacts.lnf.company.converter.CompanyEventConverter;
import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.repository.CompanyEventRepository;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log
public class CompanyEventSchedulerService {

    private final CompanyEventRepository companyEventRepository;

    public List<CompanyEventDto> findEventsByTypeAndDate(EventType eventType, LocalDate dateAndTime) {
        List<CompanyEvent> entities = companyEventRepository.getEventsByDate(eventType, dateAndTime);
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
