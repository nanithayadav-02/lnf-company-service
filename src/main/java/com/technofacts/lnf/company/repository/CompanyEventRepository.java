package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface CompanyEventRepository extends JpaRepository<CompanyEvent, UUID> {

    @Query("SELECT ce FROM CompanyEvent ce WHERE ce.company.id = :id")
    List<CompanyEvent> findByCompanyId(@Param("id") UUID id);
}
