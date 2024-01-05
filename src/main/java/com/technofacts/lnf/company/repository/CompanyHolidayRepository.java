package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, UUID> {

    @Query("SELECT ad FROM CompanyHoliday ad WHERE ad.company.id = :id")
    List<CompanyHoliday> findByCompanyId(@Param("id") UUID id);

    @Query(value = "SELECT * FROM company_holiday ch " +
            "WHERE ch.location = :location AND EXTRACT(YEAR FROM ch.date) = :year", nativeQuery = true)
    List<CompanyHoliday> findByLocationAndYear(String location,long year);

    @Query(value = "SELECT * FROM company_holiday ch " +
            "WHERE ch.id = :holidayId AND ch.company_id = :companyId", nativeQuery = true)
    CompanyHoliday findByCompanyIdAndHolidayId(UUID holidayId, UUID companyId);
}
