package com.technofacts.lnf.company.repository;

import com.technofacts.lnf.company.model.CompanyAddress;
import com.technofacts.lnf.company.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyAddressRepository extends JpaRepository<CompanyAddress, UUID> {

    @Query("SELECT ad FROM CompanyAddress ad WHERE ad.company.id = :id")
    List<CompanyAddress> findByCompanyId(@Param("id") UUID id);

    @Query("SELECT ad FROM CompanyAddress ad WHERE ad.company.id = :id AND ad.type = :type")
    Optional<CompanyAddress> findByCompanyIdAndType(@Param("id") String UUID,
                                                    @Param("type") AddressType type);

}
