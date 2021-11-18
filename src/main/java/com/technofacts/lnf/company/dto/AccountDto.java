package com.technofacts.lnf.company.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private UUID id;

    @NotNull(message = "number cannot be null")
    @NotBlank(message = "number cannot be blank")
    private String number;

    @NotNull(message = "branch cannot be null")
    @NotBlank(message = "branch cannot be blank")
    private String branch;

    @NotNull(message = "ifscCode cannot be null")
    @NotBlank(message = "ifscCode cannot be blank")
    private String ifscCode;

    @NotNull(message = "ibanNumber cannot be null")
    @NotBlank(message = "ibanNumber cannot be blank")
    private String ibanNumber;

    @NotNull(message = "address cannot be null")
    @NotBlank(message = "address cannot be blank")
    private String address;

}
