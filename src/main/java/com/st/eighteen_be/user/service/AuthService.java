package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.AuthenticationJwtException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.OccupiedException;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.service.RefreshTokenService;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.enums.RolesType;
import com.st.eighteen_be.user.repository.TokenBlackList;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final UserService userService;
    private final MediaDataService mediaDataService;
    private final EncryptService encryptService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackList tokenBlackList;

    @Value("${jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    public boolean isDuplicationUniqueId(String uniqueId){
        try {
            userService.findByUniqueId(uniqueId);
            return true;

        } catch (NotFoundException e) {
            return false;
        }
    }

    @Transactional
    public JwtTokenDto save(@NotNull SignUpRequestDto requestDto, @Nullable List<String> keys) {
        try {
            UserInfo user = requestDto.toEntity(
                    encryptService.encryptPhoneNumber(requestDto.phoneNumber()),
                    CategoryType.of(requestDto.category()));

            UserRoles userRoles = UserRoles.builder()
                    .role(RolesType.USER)
                    .build();

            userRoles.setUser(user);

            if(keys != null){
                mediaDataService.addMediaData(user,keys);
            }

            userService.save(user);

            return signIn(requestDto.phoneNumber());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")) {
                throw new OccupiedException(ErrorCode.EXISTS_USER);
            }
            throw e;
        }
    }

    @Transactional
    public JwtTokenDto signIn(String phoneNumber) {
        String encryptPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        log.info("encryptPhoneNumber->{}",encryptPhoneNumber);

        UserInfo userInfo = userService.findByPhoneNumber(encryptPhoneNumber);

        Authentication authentication = createAuthentication(userInfo.getUniqueId());

        log.info("authentication-> {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);

        // 4. RefreshToken 저장
        refreshTokenService.setRefreshToken(authentication, token.getRefreshToken(), refreshTokenExpireTime);

        // 5. 토큰 발급
        return token;
    }

    @Transactional
    public void signOut(String accessToken) {

        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);

        // 2. SecurityContextHolder 에서 authentication 을 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 3. DB에 저장된 Refresh Token 제거
        refreshTokenService.deleteRefreshToken(authentication.getName());

        // 4. Access Token blacklist에 등록하여 만료시키기
        // 해당 엑세스 토큰의 남은 유효시간을 얻음
        Long expiration = jwtTokenProvider.getExpiration(requestAccessToken);
        tokenBlackList.setBlackList(requestAccessToken, authentication.getName(), expiration);

    }

    public JwtTokenDto reissue(String accessToken, String refreshToken) {
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);
        String requestRefreshToken = jwtTokenProvider.resolveRefreshToken(refreshToken);

        try {
            // 1. Refresh Token 검증
            jwtTokenProvider.validateToken(requestRefreshToken);

            // 2. 액세스 토큰으로 Authentication 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

            // 4. 저장소에서 User Unique ID 를 기반으로 Refresh Token 값 가져옴
            String getRefreshToken = refreshTokenService.getRefreshToken(authentication.getName());

            // 5. Refresh Token 일치하는지 검사
            if (!getRefreshToken.equals(requestRefreshToken)) {
                throw new AuthenticationJwtException(ErrorCode.UNAUTHORIZED_TOKEN);
            }

            // 6. 새로운 토큰 생성
            JwtTokenDto newToken = jwtTokenProvider.generateToken(authentication);

            // 7. 리프레시 토큰에서 유효기간 뽑아오기
            long expiration = jwtTokenProvider.getExpiration(newToken.getRefreshToken());

            // 8. 저장소 정보 업데이트
            refreshTokenService.setRefreshToken(authentication,newToken.getRefreshToken(),expiration);

            return newToken;

        } catch (SecurityException | MalformedJwtException e){
            throw new BadRequestException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Authentication createAuthentication(String uniqueId){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(uniqueId, "");

        return authenticationManager.authenticate(authenticationToken);
    }
}
