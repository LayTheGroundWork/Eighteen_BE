package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserRepository userRepository;

    //TODO: mbti, 자기소개글, 질문리스트 작성(최소 3개) 추가 및 수정 기능 만들기
    // 마이 페이지는 첫 단계에서는 프론트에서 넘어온 값만 저장하면 됨
    // 이미 값이 존재하고 수정 api를 호출할 회원 id와 어떤 항목을 수정할건지 넘겨주면 됨



}
