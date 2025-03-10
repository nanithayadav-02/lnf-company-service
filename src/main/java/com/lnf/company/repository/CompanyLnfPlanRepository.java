package com.lnf.company.repository;

import com.lnf.company.model.CompanyLnfPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyLnfPlanRepository extends JpaRepository<CompanyLnfPlan, UUID> {

    @Query("select cp from CompanyLnfPlan cp where cp.company.id = :id")
    List<CompanyLnfPlan> findByCompanyId(@Param("id") UUID id);
}
