package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.PayrollComponentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollComponentConfigurationRepository extends JpaRepository<PayrollComponentConfiguration, UUID> {

    @Query("SELECT ce FROM PayrollComponentConfiguration ce WHERE ce.company.id = :id")
    List<PayrollComponentConfiguration> findByCompanyId(@Param("id") UUID id);



}
