package com.brahmosbhawan.security;

import com.brahmosbhawan.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String studentId;
    private final String name;
    private final String email;
    private final String password;
    private final String roomNumber;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.studentId = user.getStudentId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.roomNumber = user.getRoomNumber();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    public Long getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
