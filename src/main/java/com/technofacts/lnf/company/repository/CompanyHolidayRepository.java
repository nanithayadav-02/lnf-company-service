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
}
