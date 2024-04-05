package com.technofacts.lnf.company.repository;

import java.util.List;
import java.util.UUID;

import com.technofacts.lnf.company.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

    @Query("SELECT g FROM Account g WHERE g.company.id = :id")
    List<Account> findByCompanyId(@Param("id") UUID id);
}
