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

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND" +
            " EXTRACT(DATE FROM ce.dateAndTime) = CURRENT_DATE")
    List<CompanyEvent> findEventsByCurrentDate(@Param("eventType") EventType eventType);

    @Query(value = "SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND" +
            " ce.dateAndTime = :dateAndTime")
    List<CompanyEvent> findEventsByDate(@Param("eventType") EventType eventType,
                                        @Param("dateAndTime") LocalDate dateAndTime);

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND" +
            " ce.dateAndTime BETWEEN :startDate AND :endDate")
    List<CompanyEvent> findEventsByEventTypeAndDateRange(@Param("eventType") EventType eventType,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:week IS NULL OR EXTRACT(WEEK FROM e.dateAndTime) = :week)")
    List<CompanyEvent> findEventsByWeek(@Param("eventType") EventType eventType, @Param("week") Integer week);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:month IS NULL OR EXTRACT(MONTH FROM e.dateAndTime) = :month)")
    List<CompanyEvent> findEventsByMonth(@Param("eventType") EventType eventType, @Param("month") Integer month);

    @Query("SELECT e FROM CompanyEvent e WHERE " +
            "(:eventType IS NULL OR e.eventType = :eventType) AND " +
            "(:year IS NULL OR EXTRACT(YEAR FROM e.dateAndTime) = :year)")
    List<CompanyEvent> findEventsByYear(@Param("eventType") EventType eventType, @Param("year") Integer year);

    @Query("SELECT e FROM CompanyEvent e WHERE e.eventType = :eventType AND " +
            "EXTRACT(MONTH FROM e.dateAndTime) = :month AND EXTRACT(YEAR FROM e.dateAndTime) = :year")
    List<CompanyEvent> findEventsByMonthAndYear(@Param("eventType") EventType eventType, @Param("month") int month,
                                                @Param("year") int year);

    @Query("SELECT e FROM CompanyEvent e WHERE e.eventType = :eventType")
    List<CompanyEvent> findEventsByType(@Param("eventType") EventType eventType);
}
