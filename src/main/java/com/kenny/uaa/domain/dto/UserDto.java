package com.kenny.uaa.domain.dto;

import com.kenny.uaa.annotation.PasswordMatch;
import com.kenny.uaa.annotation.ValidEmail;
import com.kenny.uaa.annotation.ValidPassword;
import com.kenny.uaa.util.Constants;
import lombok.Data;

import javax.validation.constraints.*;

import java.io.Serializable;
@PasswordMatch
@Data
public class UserDto implements Serializable {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    private String matchingPassword;

    @NotNull
//    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @ValidEmail
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 50, message = "Name must be between 4 and 50 characters")
    private String name;

    @Pattern(regexp = Constants.PATTERN_MOBILE)
    @NotNull
    private String mobile;
}
