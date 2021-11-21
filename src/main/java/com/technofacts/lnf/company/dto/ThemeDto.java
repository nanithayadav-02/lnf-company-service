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
public class ThemeDto {

    private UUID id;

    @NotNull(message = "type cannot be null")
    @NotBlank(message = "type cannot be blank")
    private String type;

    @NotNull(message = "value cannot be null")
    @NotBlank(message = "value cannot be blank")
    private String value;
}
