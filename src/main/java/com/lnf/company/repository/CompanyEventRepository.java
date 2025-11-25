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

package com.lnf.company.repository;

import com.lnf.company.model.enums.EventType;
import com.lnf.company.model.CompanyEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CompanyEventRepository extends JpaRepository<CompanyEvent, UUID> {

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.company.id = :id")
    List<CompanyEvent> findByCompanyId(@Param("id") UUID id);

    @Query("SELECT ce FROM CompanyEvent ce WHERE (:email IS NULL OR ce.createdBy = :email) AND" +
            " EXTRACT(DATE FROM ce.dateAndTime) = CURRENT_DATE")
    List<CompanyEvent> findEventsByCurrentDateAndEmail(String email);

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.eventType = :eventType AND" +
            " EXTRACT(DATE FROM ce.dateAndTime) = CURRENT_DATE")
    List<CompanyEvent> findEventsByCurrentDateAndEmail(@Param("eventType") EventType eventType);

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

    @Query("select ce from CompanyEvent ce where ce.createdBy = :email")
    Page<CompanyEvent> findCompanyEventsWithPagination(String email, Pageable pageable);

}
