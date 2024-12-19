package com.kenny.uaa.annotation;

import com.kenny.uaa.util.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("authentication.name == #user.username or " +
        "hasAnyAuthority('" + Constants.ROLE_ADMIN + "' , '" + Constants.AUTHORITY_USER_UPDATE +
        "')")
public @interface RoleAdminOrSelfWithUserParam {
}
