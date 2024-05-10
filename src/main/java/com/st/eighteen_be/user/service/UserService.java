package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.user.domain.dto.LoginRequestDto;
import com.st.eighteen_be.user.domain.dto.signIn.SignInRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
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
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    /** 가입 여부 확인 */
    private boolean phoneNumberCheck(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public void save(SignInRequestDto requestDto){

        try{
            userRepository.save(requestDto.toEntity(passwordEncoder.encode(requestDto.password())));

        } catch (DataIntegrityViolationException e){
            if(e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")){
                throw new OccupiedException(ErrorCode.EXISTS_MEMBER);
            }
            throw e;
        }
    }

    // 수정 필요
    @Transactional(readOnly = true)
    public void login(LoginRequestDto requestDto){

        userRepository.findByPhoneNumber(requestDto.phoneNumber()).orElseThrow();
    }
}
