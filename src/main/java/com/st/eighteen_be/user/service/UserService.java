package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.jwt.Jwt;
import com.st.eighteen_be.jwt.JwtProvider;
import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.domain.dto.LoginRequestDto;
import com.st.eighteen_be.user.domain.dto.signUp.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserPrivacy save(SignUpRequestDto requestDto){

        try{
            return userRepository.save(requestDto.toEntity(passwordEncoder.encode(requestDto.password())));

        } catch (DataIntegrityViolationException e){
            if(e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")){
                throw new OccupiedException(ErrorCode.EXISTS_MEMBER);
            }
            throw e;
        }
    }

    // 수정 필요
    @Transactional(readOnly = true)
    public Jwt signIn(LoginRequestDto requestDto){

        // 1. phoneNumber + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authentication 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.phoneNumber(), requestDto.password());

        // 2. 실제 검증 authentication() 메서드를 통해 요청된 User 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByPhoneNumber 메서드 실행
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 생성 후 반환
        return jwtProvider.generateToken(authentication);
    }
}
