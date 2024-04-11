package com.st.eighteen_be.member.service;

import com.st.eighteen_be.member.domain.MemberRepository;
import com.st.eighteen_be.member.domain.dto.MemberResponseDto;
import com.st.eighteen_be.member.domain.dto.signIn.SignInRequestDto;
import com.st.eighteen_be.member.domain.dto.signIn.SignInResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.st.eighteen_be.service
 * fileName       : MemberService
 * author         : ehgur
 * date           : 2024-04-15
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-15        ehgur             최초 생성
 */

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public SignInResponseDto signIn(SignInRequestDto requestDto){
        // 나중에 개인정보 Table 만들면 거기서 회원 Id를 통해 전화번호 비교하기


        memberRepository.save(requestDto.toEntity());
        return requestDto;
    }

    public MemberResponseDto findByPhoneNumber(String phoneNumber){

    }

}
