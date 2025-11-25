/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.company.repository;

import com.lnf.company.model.CompanyNotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanyNotesRepository extends JpaRepository<CompanyNotes, UUID> {
    @Query("select t from CompanyNotes t where t.company.id = :id")
    List<CompanyNotes> findByCompanyId(@Param("id") UUID id);

    @Query("select cn from CompanyNotes cn where cn.createdBy = :email")
    Page<CompanyNotes> findCompanyNotesWithPagination(String email, Pageable pageable);

}
