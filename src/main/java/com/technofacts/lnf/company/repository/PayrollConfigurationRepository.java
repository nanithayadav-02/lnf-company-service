package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyEvent;
import com.technofacts.lnf.company.model.PayrollConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollConfigurationRepository extends JpaRepository<PayrollConfiguration, UUID> {

    @Query("SELECT ce FROM PayrollConfiguration ce WHERE ce.company.id = :id")
    PayrollConfiguration findByCompanyId(@Param("id") UUID id);



}
