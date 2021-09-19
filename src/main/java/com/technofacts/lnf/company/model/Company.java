package com.technofacts.lnf.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.*;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company extends AuditableEntity {

    @Column(name = "company_id", nullable = false, unique = true)
    private String companyId;
}