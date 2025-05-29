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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lnf.company.model.CompanyAddress;
import com.lnf.company.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, UUID> {

    @Query("SELECT ad FROM CompanyAddress ad WHERE ad.company.id = :id")
    List<CompanyAddress> findByCompanyId(@Param("id") UUID id);

    @Query("SELECT ad FROM CompanyAddress ad WHERE ad.company.id = :id AND ad.type = :type")
    Optional<CompanyAddress> findByCompanyIdAndType(@Param("id") String uuid,
                                                    @Param("type") AddressType type);

}
