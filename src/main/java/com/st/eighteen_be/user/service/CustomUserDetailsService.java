package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(this::createUserDetails)
                .orElseThrow(() ->
                        new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(UserPrivacy userPrivacy) {
        return User.builder()
                .username(userPrivacy.getUsername())
                .password(userPrivacy.getPassword())
                .roles(userPrivacy.getRoles().toArray(new String[0]))
                .build();
    }

}
