package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.AuthenticationException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.domain.RefreshToken;
import com.st.eighteen_be.token.service.RefreshTokenService;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.repository.TokenBlackList;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    private final TokenBlackList tokenBlackList;


    public UserInfo save(SignUpRequestDto requestDto) {
        try {
            return userRepository.save(requestDto.toEntity(
                    encryptService.encryptPhoneNumber(requestDto.phoneNumber()))
            );
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")) {
                throw new OccupiedException(ErrorCode.EXISTS_USER);
            }
            throw e;
        }
    }

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
        refreshTokenService.saveOrUpdate(authentication);

        // RefreshToken Cookie에 저장
        // jwtTokenProvider.setRefreshTokenAtCookie(refreshToken);

        // 5. 토큰 발급
        return token;
    }

    public void signOut(HttpServletRequest request) {

        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);

        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }

        // 2. Access Token 에서 authentication 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        // 3. DB에 저장된 Refresh Token 제거
        refreshTokenService.deleteRefreshTokenById(authentication.getName());

        // 4. Access Token blacklist에 등록하여 만료시키기
        // 해당 엑세스 토큰의 남은 유효시간을 얻음
        Long expiration = jwtTokenProvider.getExpiration(requestAccessToken);
        tokenBlackList.setBlackList(requestAccessToken, authentication.getName(), expiration);

    }

    public JwtTokenDto reissue(HttpServletRequest request) {

        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);
        String requestRefreshToken = jwtTokenProvider.resolveRefreshToken(request);

        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new NotValidException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }

        // 2. Access Token 에서 User ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        // 3. 저장소에서 User ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenService.findRefreshTokenById(authentication.getName());

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getRefreshToken().equals(requestRefreshToken)) {
            throw new AuthenticationException(ErrorCode.TOKEN_UNAUTHORIZED);
        }

        // 5. 새로운 토큰 생성
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);

        // 6. 저장소 정보 업데이트
        refreshTokenService.saveOrUpdate(authentication);

        return token;
    }

    public UserDetailsResponseDto findByUniqueId(String uniqueId) {

        UserInfo userInfo = userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        return new UserDetailsResponseDto(userInfo);
    }

    public UserProfileResponseDto findUserProfileByUniqueId(String uniqueId) {
        UserInfo userInfo = userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );

        return new UserProfileResponseDto(userInfo);
    }

    public List<UserProfileResponseDto> findAll() {
        List<UserInfo> users = userRepository.findAll();
        return users.stream()
                .map(UserProfileResponseDto::new)
                .collect(Collectors.toList());
    }

}
