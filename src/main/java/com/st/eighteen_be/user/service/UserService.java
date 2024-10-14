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
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.enums.RolesType;
import com.st.eighteen_be.user.repository.TokenBlackList;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public boolean isDuplicationUniqueId(String uniqueId){
        Optional<UserInfo> user = userRepository.findByUniqueId(uniqueId);

        return user.isPresent();
    }

    public JwtTokenDto save(@NotNull SignUpRequestDto requestDto, List<String> keys) {
        try {

            UserInfo user = requestDto.toEntity(
                    encryptService.encryptPhoneNumber(requestDto.phoneNumber()),
                    CategoryType.of(requestDto.category()));

            UserRoles userRoles = UserRoles.builder()
                    .role(RolesType.USER)
                    .build();

            userRoles.setUser(user);

            //TODO: 업로드한 이미지가 없을 경우 기본 이미지 경로에 대한 key 값을 저장해야함
            addMediaData(user,keys);

            userRepository.save(user);

            return signIn(requestDto.phoneNumber());

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().toUpperCase().contains("PHONE_NUMBER_UNIQUE")) {
                throw new OccupiedException(ErrorCode.EXISTS_USER);
            }
            throw e;
        }
    }

    public JwtTokenDto signIn(String phoneNumber) {
        String encryptPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        log.info("encryptPhoneNumber->{}",encryptPhoneNumber);

        UserInfo userInfo = userRepository.findByPhoneNumber(encryptPhoneNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );

        // 1. phoneNumber와 verificationCode를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userInfo.getUniqueId(), "");

        log.info("UsernamePasswordAuthenticationToken-> {}", authenticationToken);

        // 2. 실제 검증 authentication() 메서드를 통해 요청된 User 에 대한 검증 진행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        log.info("authentication-> {}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtTokenDto token = jwtTokenProvider.generateToken(authentication);

        // 4. RefreshToken 저장
        refreshTokenService.saveOrUpdate(authentication);

        // 5. 토큰 발급
        return token;
    }

    public void signOut(String accessToken) {

        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);

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

    public JwtTokenDto reissue(String refreshToken) {
        String requestRefreshToken = jwtTokenProvider.resolveRefreshToken(refreshToken);

        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new NotValidException(ErrorCode.REFRESH_TOKEN_NOT_VALID);
        }

        // 2. SecurityContextHolder 에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 3. 저장소에서 User ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken token = refreshTokenService.findRefreshTokenById(authentication.getName());

        // 4. Refresh Token 일치하는지 검사
        if (!token.getRefreshToken().equals(requestRefreshToken)) {
            throw new AuthenticationException(ErrorCode.TOKEN_UNAUTHORIZED);
        }

        // 5. 새로운 토큰 생성
        JwtTokenDto newToken = jwtTokenProvider.generateToken(authentication);

        // 6. 저장소 정보 업데이트
        refreshTokenService.saveOrUpdate(authentication);

        return newToken;
    }

    public Set<String> getRoles(UserInfo userInfo){
        Set<UserRoles> userRoles = userInfo.getRoles();
        Set<String> roles = new HashSet<>();

        for(UserRoles userRole : userRoles){
            roles.add(userRole.getRole().getValue());
        }

        return roles;
    }

    public void addMediaData(UserInfo user, List<String> keys){
        for(String imageKey : keys){
            UserMediaData userMediaData = UserMediaData.builder()
                    .imageKey(imageKey)
                    .isThumbnail(false)
                    .build();

            userMediaData.setUser(user);
        }
    }

    public UserInfo findByToken(String accessToken) {
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);
        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        return userRepository.findByUniqueId(authentication.getName()).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );
    }

    public UserInfo findByUniqueIdToEntity(String uniqueId){
        return userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );
    }

    public UserInfo findByIdToEntity(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );
    }

    public UserInfo findById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    public UserInfo findByUniqueId(String uniqueId) {
        return userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    public List<UserInfo> findAll(){
        return userRepository.findAll();
    }

    public List<UserInfo> findAllByCategory(CategoryType category){
        return userRepository.findAllByCategory(category);
    }

//    public UserMediaData findByImageKey(String imageKey){
//        return userRepository.findByImageKey(imageKey);
//    }

    int findLikeCountById(Integer id) {
        return userRepository.findById(id).map(UserInfo::getLikeCount).orElse(0);
    }
}
