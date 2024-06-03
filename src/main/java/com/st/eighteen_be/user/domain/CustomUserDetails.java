package com.st.eighteen_be.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class CustomUserDetails extends UserInfo implements UserDetails {
    private Integer id;
    private String phoneNumber;
    private List<String> roles;
    private String password;

    private CustomUserDetails(UserInfo userInfo, String encodePassword) {
        this.id = userInfo.getId();
        this.phoneNumber = userInfo.getPhoneNumber();
        this.password = encodePassword;
        this.roles = userInfo.getRoles();
    }

    private CustomUserDetails(String phoneNumber, List<String> roles) {
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    private CustomUserDetails(String phoneNumber, String password, List<String> roles) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.roles = roles;
    }

    public static CustomUserDetails of(UserInfo userInfo, String encodePassword) {
        return new CustomUserDetails(userInfo,encodePassword);
    }

    public static CustomUserDetails of(String phoneNumber, List<String> roles) {
        return new CustomUserDetails(phoneNumber, roles);
    }

    public static CustomUserDetails of(String phoneNumber, String password, List<String> roles) {
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
        return this.phoneNumber;
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
