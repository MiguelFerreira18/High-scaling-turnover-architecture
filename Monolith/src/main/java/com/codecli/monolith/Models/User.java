package com.codecli.monolith.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.UuidGenerator;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int nif;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private List<Authority> authorities = new ArrayList<>();

    public User(String id, String email, String name, int nif, List<Authority> authorities, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        try {
            this.nif = isNifValid(nif);
        } catch (Exception e) {
            this.nif = 999999990;
        }
        this.authorities = authorities;
        this.password = password;
    }

    public User(String email, String name, List<Authority> authorities, String password, int nif) {
        this.email = email;
        this.name = name;
        try {
            this.nif = isNifValid(nif);
        } catch (Exception e) {
            this.nif = 999999990;
        }
        this.authorities = authorities;
        this.password = password;
    }

    public User(String email, String name, String password, int nif) {
        this.email = email;
        this.name = name;
        try {
            this.nif = isNifValid(nif);
        } catch (Exception e) {
            this.nif = 999999990;
        }
        this.authorities = new ArrayList<>();
        this.password = password;
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.nif = 999999990;
        this.authorities = new ArrayList<>();
        this.password = password;
    }

    public User() {
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public int getNif() {
        return nif;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPassword(String password, PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void setNif(int nif) {
        this.nif = nif;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    private int isNifValid(int nif) {
        String sNif = String.valueOf(nif);
        if (sNif.length() != 9) {
            throw new RuntimeException("Invalid nif: Isn't of correct size");
        }

        int firstDigit = sNif.charAt(0) - '0';
        if (firstDigit < 1 || firstDigit > 7) {
            throw new RuntimeException("Invalid nif: Doesn't belong to any group");
        }

        int sum = 0;
        for (int i = 0; i < sNif.length() - 1; i++) {
            int val = Integer.parseInt(sNif.substring(i, i + 1));
            sum += val * (9 - i);
        }

        int remainder = sum % 11;
        int checkDigit = (remainder == 0 || remainder == 1) ? 0 : 11 - remainder;
        if (checkDigit == Integer.parseInt(sNif.substring(8, 9))) {
            return nif;
        } else {
            throw new RuntimeException("Invalid nif: Failed checksum");
        }
    }
}
