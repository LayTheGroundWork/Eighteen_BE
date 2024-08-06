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
    private Set<String> roles;
    private String password;

    private CustomUserDetails(UserInfo userInfo, String encodePassword) {
        this.id = userInfo.getId();
        this.uniqueId = userInfo.getUniqueId();
        this.password = encodePassword;
        this.roles = userInfo.getRoles();
    }

    private CustomUserDetails(String uniqueId, Set<String> roles) {
        this.uniqueId = uniqueId;
        this.roles = roles;
    }

    private CustomUserDetails(String uniqueId, String password, Set<String> roles) {
        this.uniqueId = uniqueId;
        this.password = password;
        this.roles = roles;
    }

    public static CustomUserDetails of(UserInfo userInfo, String encodePassword) {
        return new CustomUserDetails(userInfo,encodePassword);
    }

    public static CustomUserDetails of(String phoneNumber, Set<String> roles) {
        return new CustomUserDetails(phoneNumber, roles);
    }

    public static CustomUserDetails of(String phoneNumber, String password, Set<String> roles) {
        return new CustomUserDetails(phoneNumber, password, roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
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
