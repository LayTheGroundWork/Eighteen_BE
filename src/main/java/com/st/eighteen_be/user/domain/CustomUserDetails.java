package com.st.eighteen_be.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class CustomUserDetails extends UserInfo implements UserDetails {
    private Integer id;
    private String uniqueId;
    private String password;
    private Set<UserRoles> roles;

    private CustomUserDetails(UserInfo userInfo, String encodePassword, Set<UserRoles> roles) {
        this.id = userInfo.getId();
        this.uniqueId = userInfo.getUniqueId();
        this.password = encodePassword;
        this.roles = roles;
    }

    public static CustomUserDetails of(UserInfo userInfo, String encodePassword, Set<UserRoles> roles) {
        return new CustomUserDetails(userInfo,encodePassword,roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(userRoles -> new SimpleGrantedAuthority(userRoles.getRole().getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.uniqueId;
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
