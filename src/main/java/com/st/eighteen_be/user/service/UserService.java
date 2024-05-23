package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.domain.RefreshToken;
import com.st.eighteen_be.token.service.RefreshTokenService;
import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.dto.sign.SignInRequestDto;
import com.st.eighteen_be.user.dto.sign.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final EncryptService encryptService;


    public UserPrivacy save(SignUpRequestDto requestDto) {
        try {
            return userRepository.save(requestDto.toEntity(
                    encryptService.encryptPhoneNumber(requestDto.phoneNumber()))
            );
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")) {
                throw new OccupiedException(ErrorCode.SIGN_UP_EXISTS_USER);
            }
            throw e;
        }
    }

    @Transactional
    public JwtTokenDto signIn(SignInRequestDto requestDto) {

        // 1. phoneNumber와 verificationCode를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                //new UsernamePasswordAuthenticationToken(requestDto.phoneNumber(), smsUtil.verifySms(requestDto));
                new UsernamePasswordAuthenticationToken(requestDto.phoneNumber(), "");

        log.info("UsernamePasswordAuthenticationToken-> {}", authenticationToken);

        // 2. 실제 검증 authentication() 메서드를 통해 요청된 User 에 대한 검증 진행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        log.info("authentication-> {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = refreshTokenService.saveOrUpdate(authentication);
        jwtTokenProvider.setRefreshTokenAtCookie(refreshToken);

        // 5. 토큰 발급
        return token;
    }

    public Optional<UserPrivacy> findByPhoneNumber(String encryptPhoneNumber) {
        return userRepository.findByPhoneNumber(encryptPhoneNumber);
    }
}
