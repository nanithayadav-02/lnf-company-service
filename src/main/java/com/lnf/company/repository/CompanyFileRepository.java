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

import com.lnf.company.model.CompanyFile;
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
