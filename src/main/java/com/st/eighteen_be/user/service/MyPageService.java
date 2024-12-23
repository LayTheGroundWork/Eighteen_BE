package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserDtoService userDtoService;

    @Transactional(readOnly = true)
    public UserDetailsResponseDto view(String uniqueId){
        return userDtoService.findByUniqueId(uniqueId);
    }

    public void update(String uniqueId, MyPageRequestDto requestDto) {
        userDtoService.myPageUpdate(uniqueId, requestDto);
    }

    // 프로필 이미지 삭제
    public void profileDelete(String uniqueId, String imageKey) {
        userDtoService.profileDelete(uniqueId, imageKey);
    }

    // 대표 이미지 수정




}
