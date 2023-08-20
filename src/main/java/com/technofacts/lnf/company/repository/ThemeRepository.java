package com.technofacts.lnf.company.repository;

import java.util.List;
import java.util.UUID;

import com.technofacts.lnf.company.model.Company;
import com.technofacts.lnf.company.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ThemeRepository extends JpaRepository<Theme, UUID>, JpaSpecificationExecutor<Company> {

    @Query("select t from Theme t where t.company.id = :id")
    List<Theme> findByCompanyId(@Param("id") UUID id);

}
