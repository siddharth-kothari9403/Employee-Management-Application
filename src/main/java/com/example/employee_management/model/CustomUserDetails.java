package com.example.employee_management.model;

import com.example.employee_management.entity.User;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Integer getEmployeeId() {
        return user.getEmployeeDetails() != null ? user.getEmployeeDetails().getId() : null;
    }

    @Override
    @NullUnmarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    @NullUnmarked
    public String getUsername() {
        return user.getUsername();
    }
}
