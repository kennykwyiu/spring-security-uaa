package com.kenny.uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity class, implementing the GrantedAuthority interface
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "uaa_roles")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Auto-increment ID, unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name with unique constraint, cannot be duplicated
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;

    @JsonIgnore
    @Fetch(FetchMode.JOIN)
    @ManyToMany
    @JoinTable(name = "uaa_roles_permissions",
            joinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")}
    )
    private Set<Permission> permissions = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Role> users;
}