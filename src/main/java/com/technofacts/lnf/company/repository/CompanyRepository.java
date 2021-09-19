package com.technofacts.lnf.company.repository;

import java.util.Optional;
import java.util.UUID;

import com.technofacts.lnf.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

    @Query("select e from Company e where e.companyId = :company_id")
    Optional<Company> findByCompanyId(@Param("company_id") String company_id);

}
