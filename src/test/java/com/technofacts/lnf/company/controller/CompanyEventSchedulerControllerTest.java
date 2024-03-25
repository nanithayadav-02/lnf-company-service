package com.technofacts.lnf.company.controller;

import com.technofacts.lnf.company.BaseTestClass;
import com.technofacts.lnf.company.model.enums.EventType;
import com.technofacts.lnf.company.service.CompanyEventSchedulerService;
import com.technofacts.lnf.dto.company.CompanyEventDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CompanyEventSchedulerControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CompanyEventSchedulerService service;

    @BeforeAll
    void beforeAll() {
       //TobeImplemented
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void findEventsByDate () throws Exception {

        EventType eventType = EventType.Birthday;
        LocalDate date = LocalDate.of(2024, 4, 2);
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/date";

        given(service.findEventsByTypeAndDate(eventType, date)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                .param("eventType", String.valueOf(eventType))
                .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByTypeAndDate(eventType, date);

    }

    @Test
    void findEventsByEventTypeAndDateRange () throws Exception {
        EventType eventType = EventType.Birthday;
        LocalDate startDate = LocalDate.of(2024, 3, 25);
        LocalDate endDate = LocalDate.of(2024, 4, 2);
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/startDate/endDate";

        given(service.findEventsByEventTypeAndDateRange(eventType, startDate, endDate)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .param("eventType", String.valueOf(eventType))
                        .param("startDate", startDate.toString())
                        .param("endDate",endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByEventTypeAndDateRange(eventType, startDate, endDate);
    }

    @Test
    void findEventsByWeek () throws Exception {
        EventType eventType = EventType.Birthday;
        int week = 1;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/week";

        given(service.findEventsByWeek(eventType, week)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .param("eventType", String.valueOf(eventType))
                        .param("week", String.valueOf(week)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByWeek(eventType, week);
    }

    @Test
    void findEventsByMonth () throws Exception {
        EventType eventType = EventType.Birthday;
        int month = 2;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/month";

        given(service.findEventsByMonth(eventType, month)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .param("eventType", String.valueOf(eventType))
                        .param("month", String.valueOf(month)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByMonth(eventType, month);
    }

    @Test
    void findEventsByYear () throws Exception {
        EventType eventType = EventType.Birthday;
        int year = 2024;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/year";

        given(service.findEventsByYear(eventType, year)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .param("eventType", String.valueOf(eventType))
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByYear(eventType, year);
    }

    @Test
    void findEventsByMonthAndYear () throws Exception {
        EventType eventType = EventType.Birthday;
        int month = 2;
        int year = 2024;
        CompanyEventDto event1 = mockEvent1();
        CompanyEventDto event2 = mockEvent2();
        List<CompanyEventDto> events = List.of(event1, event2);

        String url = "/lnf/companyEvent/eventType/month/year";

        given(service.findEventsByMonthAndYear(eventType, month, year)).willReturn(events);

        String resultContent = new String(Files
                .readAllBytes(Paths.get(ClassLoader.getSystemResource("testdata/company-events-scheduler.json")
                        .toURI())));

        mockMvc.perform(get(url)
                        .param("eventType", String.valueOf(eventType))
                        .param("month", String.valueOf(month))
                        .param("year", String.valueOf(year)))
                .andExpect(status().isOk())
                .andExpect(content().json(resultContent));

        verify(service, times(1)).findEventsByMonthAndYear(eventType, month, year);
    }

    private CompanyEventDto mockEvent1() {
        return  createEvent("019d9f96-8f91-4725-9056-ed022b4cb65f", "Pritham k Shende",
                "25th birthday");
    }

    private CompanyEventDto mockEvent2() {
        return  createEvent("9060225c-f356-45b4-a89a-ef91ea859e60", "Kushbu Sharma",
                "28th birthday");
    }

    private CompanyEventDto createEvent(String id, String assignTo, String eventDescription) {
        CompanyEventDto dto = new CompanyEventDto ();
        dto.setId(UUID.fromString(id));
        dto.setEventType("Birthday");
        dto.setDateAndTime(LocalDate.parse(("2024-04-02")));
        dto.setAssignTo(assignTo);
        dto.setEventDescription(eventDescription);
        dto.setStatus("ACTIVE");

        return dto;
    }

}
