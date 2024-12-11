package com.kenny.uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kenny.uaa.annotation.ValidEmail;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * User entity class, implementing the UserDetails interface
 */
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "uaa_users")
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Auto-increment ID, unique identifier
     */
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username
     */
    @Getter
    @Setter
    @NotNull
    @Size(max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    /**
     * Mobile number
     */
    @Getter
    @Setter
    @NotNull
    @Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$")
    @Size(min = 11, max = 11)
    @Column(length = 11, unique = true, nullable = false)
    private String mobile;

    /**
     * Name
     */
    @Getter
    @Setter
    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;

    /**
     * Whether the account is enabled, default is enabled
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Whether the account is not expired, default is not expired
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired = true;

    /**
     * Whether the account is not locked, default is not locked
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    /**
     * Whether the credentials are not expired, default is not expired
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired = true;

    /**
     * Password hash
     */
    @Getter
    @Setter
    @JsonIgnore
    @NotNull
    @Size(min = 40, max = 180)
    @Column(name = "password_hash", length = 180, nullable = false)
    private String password;

    /**
     * Email address
     */
    @Getter
    @Setter
    @ValidEmail
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true, nullable = false)
    private String email;

    @Builder.Default
    @NotNull
    @Column(name = "using_mfa", nullable = false)
    private boolean usingMfa = false;

    /**
     * mfa key
     */
    @JsonIgnore
    @Getter
    @Setter
    @NotNull
    @Column(name = "mfa_key", nullable = false)
    private String mfaKey;

    /**
     * Role list, using Set to ensure uniqueness
     */
    @Getter
    @Setter
    @JsonIgnore
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
            name = "uaa_users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}