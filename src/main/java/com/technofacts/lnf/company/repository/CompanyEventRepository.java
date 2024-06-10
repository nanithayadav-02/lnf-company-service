package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CompanyEventRepository extends JpaRepository<CompanyEvent, UUID> {

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.company.id = :id")
    List<CompanyEvent> findByCompanyId(@Param("id") UUID id);

    @Query("SELECT ce FROM CompanyEvent ce WHERE EXTRACT(DATE FROM ce.dateAndTime) = CURRENT_DATE")
    List<CompanyEvent> findEventsByCurrentDate();

    @Query(value = "SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND ce.dateAndTime = :dateAndTime")
    List<CompanyEvent> getEventsByDate(@Param("eventType") EventType eventType, @Param("dateAndTime") LocalDate dateAndTime);

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND ce.dateAndTime BETWEEN :startDate AND :endDate")
    List<CompanyEvent> getEventsByEventTypeAndDateRange(@Param("eventType") EventType eventType, @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:week IS NULL OR EXTRACT(WEEK FROM e.dateAndTime) = :week)")
    List<CompanyEvent> getEventsByWeek(@Param("eventType") EventType eventType, @Param("week") Integer week);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:month IS NULL OR EXTRACT(MONTH FROM e.dateAndTime) = :month)")
    List<CompanyEvent> getEventsByMonth(@Param("eventType") EventType eventType, @Param("month") Integer month);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:year IS NULL OR EXTRACT(YEAR FROM e.dateAndTime) = :year)")
    List<CompanyEvent> getEventsByYear(@Param("eventType") EventType eventType, @Param("year") Integer year);

    @Query("SELECT e FROM CompanyEvent e WHERE e.eventType = :eventType AND EXTRACT(MONTH FROM e.dateAndTime) = :month AND EXTRACT(YEAR FROM e.dateAndTime) = :year")
    List<CompanyEvent> getEventsByMonthAndYear(@Param("eventType") EventType eventType, @Param("month") int month,
                                               @Param("year") int year);

    @Query("SELECT e FROM CompanyEvent e WHERE e.eventType = :eventType")
    List<CompanyEvent> getEventsByType(@Param("eventType") EventType eventType);
}
