package com.kenny.uaa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyTotpDto implements Serializable {
    @NotNull
    private String mfaId;
    @NotNull
    private String code;
}
