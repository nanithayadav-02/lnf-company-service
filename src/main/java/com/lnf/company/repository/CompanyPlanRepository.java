package com.lnf.company.repository;

import com.lnf.company.model.CompanyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyPlanRepository extends JpaRepository<CompanyPlan, UUID> {

    @Query("select cp from CompanyPlan cp where cp.company.id = :id")
    List<CompanyPlan> findByCompanyId(@Param("id") UUID id);
}
