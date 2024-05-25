package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyFileRepository  extends JpaRepository<CompanyFile, UUID> {

    @Query("SELECT ce FROM CompanyFile ce WHERE ce.company.id = :id")
    List<CompanyFile> findByCompanyId(@Param("id") UUID id);

    @Query("SELECT ce FROM CompanyFile ce WHERE ce.fileName = :fileName")
    Optional<CompanyFile> findByFileName(@Param("fileName") String fileName);
}
