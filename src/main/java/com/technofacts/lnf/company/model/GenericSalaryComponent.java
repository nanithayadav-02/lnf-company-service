package com.technofacts.lnf.company.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericSalaryComponent {
    private String calculation_type;
    private String component_code;
    private String component_value;
    private String display_name;
    private String description;


}
