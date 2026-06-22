package com.codecli.monolith.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role authority;

    public Authority(Long id, Role authority) {
        this.id = id;
        this.authority = authority;
    }

    public Authority(Role authority) {
        this.authority = authority;
    }

    public Authority() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public @Nullable String getAuthority() {
        return authority.name();
    }

    public void setAuthority(Role authority) {
        this.authority = authority;
    }

    public enum Role {
        ADMIN,
        USER;
    }
}
