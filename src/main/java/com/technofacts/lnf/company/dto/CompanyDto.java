package com.technofacts.lnf.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private UUID id;

    @NotNull(message = "companyId cannot be null")
    @NotBlank(message = "companyId cannot be blank")
    private String companyId;

}
