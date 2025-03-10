package com.lnf.company.repository;

import com.lnf.company.model.LnfPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LnfPlanRepository extends JpaRepository<LnfPlan, UUID> {
}
