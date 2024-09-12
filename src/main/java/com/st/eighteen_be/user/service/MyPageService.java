package com.st.eighteen_be.user.service;

import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserDtoService userDtoService;

    @Transactional(readOnly = true)
    public UserDetailsResponseDto view(String accessToken){
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        return userDtoService.findByUniqueId(authentication.getName());
    }

    public void update(MyPageRequestDto requestDto, String accessToken) {

        UserInfo userInfo = userService.findByToken(accessToken);
        userInfo.update(requestDto);
    }
}
