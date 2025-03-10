package com.lnf.company.repository;

import com.lnf.company.model.CompanyLnfPlanAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyLnfPlanAuditRepository extends JpaRepository<CompanyLnfPlanAudit, UUID> {

    @Query("select cp from CompanyLnfPlanAudit cp where cp.companyLnfPlan.id = :id")
    List<CompanyLnfPlanAudit> findByCompanyLnfPlanId(@Param("id") UUID id);
}
