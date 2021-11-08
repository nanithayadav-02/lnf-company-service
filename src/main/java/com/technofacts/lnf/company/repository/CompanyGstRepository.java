package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyGst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyGstRepository extends JpaRepository<CompanyGst, UUID> {

    @Query("SELECT g FROM CompanyGst g WHERE g.company.companyId = :company_id")
    List<CompanyGst> findByCompanyId(@Param("company_id") String company_id);

}
