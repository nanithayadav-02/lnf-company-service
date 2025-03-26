package com.lnf.company.repository;

import com.lnf.company.model.CompanyPlanAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyPlanAuditRepository extends JpaRepository<CompanyPlanAudit, UUID> {

    @Query("select cp from CompanyPlanAudit cp where cp.companyPlan.id = :id")
    List<CompanyPlanAudit> findByCompanyPlanId(@Param("id") UUID id);

    @Query("select cp from CompanyPlanAudit cp join cp.companyPlan cpl where cpl.company.id = :companyId")
    List<CompanyPlanAudit> findByCompanyId(@Param("companyId") UUID companyId);

}
