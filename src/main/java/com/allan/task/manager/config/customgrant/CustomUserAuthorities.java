package com.allan.task.manager.config.customgrant;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserAuthorities {

    private final String username;
    private final UUID userId;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserAuthorities(String username, UUID userId,
                                 Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.userId = userId;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUserId() {
        return userId;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}