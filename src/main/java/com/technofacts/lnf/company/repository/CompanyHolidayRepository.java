package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyHoliday;
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

}
