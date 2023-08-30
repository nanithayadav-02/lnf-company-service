package com.technofacts.lnf.company.repository;

import java.util.List;
import java.util.UUID;

import com.technofacts.lnf.company.model.CompanyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPolicyRepository extends JpaRepository<CompanyPolicy, UUID> {

    @Query("SELECT pl FROM CompanyPolicy pl WHERE pl.company.id = :id")
    List<CompanyPolicy> findByCompanyId(@Param("id") UUID id);

}