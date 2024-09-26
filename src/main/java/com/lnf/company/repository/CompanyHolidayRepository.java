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

import com.lnf.company.model.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, UUID>, JpaSpecificationExecutor<CompanyHoliday> {
    @Query("SELECT ad FROM CompanyHoliday ad WHERE ad.company.id = :id")
    List<CompanyHoliday> findByCompanyId(@Param("id") UUID id);

    @Query(value = "SELECT * FROM company_holiday ch " +
            "WHERE ch.company_id = :companyId AND ch.id = :holidayId", nativeQuery = true)
    CompanyHoliday findByHolidayId(UUID companyId, UUID holidayId);

    @Query(value = "SELECT * FROM company_holiday ch " +
            "WHERE ch.company_id = :companyId AND EXTRACT(YEAR FROM ch.date) = :year AND ch.location = :location", nativeQuery = true)
    List<CompanyHoliday> findByYearAndLocation(UUID companyId, long year, String location);

    @Query(value = "SELECT * FROM company_holiday ch " +
            "WHERE ch.company_id = :companyId AND EXTRACT(YEAR FROM ch.date) = :year",nativeQuery = true)
    List<CompanyHoliday> findByYear(UUID companyId, long year);
}
