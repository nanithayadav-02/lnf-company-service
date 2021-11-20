package com.technofacts.lnf.company.repository;

import java.util.Optional;
import java.util.UUID;

import com.technofacts.lnf.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaSpecificationExecutor<Company> {

    @Query("select c from Company c where c.id = :id")
    Optional<Company> findByCompanyId(@Param("id") UUID id);

    @Query("select c from Company c where c.code = :code")
    Optional<Company> findByCompanyCode(@Param("code") String code);

}
