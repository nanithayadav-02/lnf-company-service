package com.technofacts.lnf.company.model;

import com.technofacts.lnf.company.model.enums.AddressType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@ToString
@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class Address extends AuditableEntity {

    @Column(nullable = false, name = "address_line_1")
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "town")
    private String town;

    @Column(nullable = false, name = "city")
    private String city;

    @Column(nullable = false, name = "state")
    private String state;

    @Column(nullable = false, name = "post_code")
    private String postCode;

    @Column(nullable = false, name = "country")
    private String country;

    @Column(nullable = false, name = "type")
    @Enumerated(EnumType.STRING)
    private AddressType type;

}
