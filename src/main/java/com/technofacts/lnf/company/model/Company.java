package com.technofacts.lnf.company.model;

import javax.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@ToString
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company extends AuditableEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String mobile;

    @Column
    private String website;

    @Column
    private String businessCategory;

    @Column
    private String businessDescription;

    @Column(unique = true, nullable = false)
    private String pan;

    @Column
    private String arn;

    @Column
    private LocalDate arnIssueDate;

    @Column
    private Long sacCode;

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyAddress> address = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Image> image = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<CompanyGst> gst = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<Account> account = new HashSet<>();

}
