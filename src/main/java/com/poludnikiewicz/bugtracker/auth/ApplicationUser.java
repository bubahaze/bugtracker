package com.poludnikiewicz.bugtracker.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Implementation of UserDetails enables creating and storing users
 * in database instead of using inMemory
 */

public class ApplicationUser implements UserDetails {

    private String username;
    private String password;
    private Set<? extends GrantedAuthority> grantedAuthorities;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    public ApplicationUser(String username, String password, Set<? extends GrantedAuthority> authorities,
                           boolean isAccountNonExpired, boolean isAccountNonLocked,
                           boolean isCredentialsNonExpired, boolean isEnabled) {
        this.username = username;
        this.password = password;
        this.grantedAuthorities = authorities;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }



    public Set<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {

        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }


}
