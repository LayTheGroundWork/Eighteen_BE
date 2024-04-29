package com.st.eighteen_be.member.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.member.domain.MemberPrivacy;
import com.st.eighteen_be.member.domain.dto.signIn.SignInRequestDto;
import com.st.eighteen_be.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final BCryptPasswordEncoder passwordEncoder;

    /** 가입 여부 확인 */
    private boolean phoneNumberCheck(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }


    public MemberPrivacy save(SignInRequestDto requestDto){

        try{
            return memberRepository.save(
                    requestDto.toEntity(passwordEncoder.encode(requestDto.password()))
            );
        } catch (DataIntegrityViolationException e){
            if(e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")){
                throw new OccupiedException(ErrorCode.EXISTS_MEMBER);
            } else {
                throw e;
            }
        }
    }



}
