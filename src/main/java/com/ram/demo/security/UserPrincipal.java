package com.ram.demo.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ram.demo.entity.User;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id          = user.getId();
        this.email       = user.getEmail();
        this.password    = user.getPasswordHash();
        this.active      = user.isActive();
        this.authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    // ── THIS IS THE METHOD YOUR CONTROLLERS NEED ──
    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    // ── UserDetails interface ──
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}