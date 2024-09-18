package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyNotesRepository extends JpaRepository<CompanyNotes, UUID> {
    @Query("select t from CompanyNotes t where t.company.id = :id")
    List<CompanyNotes> findByCompanyId(@Param("id") UUID id);
}
