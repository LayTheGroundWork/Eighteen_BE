package com.st.eighteen_be.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.token.domain.RefreshToken;
import com.st.eighteen_be.token.repository.RefreshTokenRepository;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignInRequestDto;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("UserService 테스트")
@ActiveProfiles("Test")
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UsernamePasswordAuthenticationToken authenticationToken;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private EncryptService encryptService;

    private SignUpRequestDto signUpRequestDto;

    private SignInRequestDto signInRequestDto;

    private ObjectMapper objectMapper;

    private JwtTokenDto jwtTokenDto;

    private final String phoneNumber = "01012341234";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String birthday = "2018-12-15T10:10";
        String verificationCode = "123456";

        signUpRequestDto = SignUpRequestDto.builder()
                .phoneNumber(phoneNumber)
                .birthDay(LocalDateTime.parse(birthday))
                .build();

        signInRequestDto = SignInRequestDto.builder()
                .phoneNumber(phoneNumber)
                .verificationCode(verificationCode)
                .build();

        jwtTokenDto = JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .accessTokenExpiresIn(1000)
                .refreshToken("refreshToken")
                .build();

        // ObjectMapper에 JavaTimeModule 등록
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("회원가입 서비스 테스트")
    void user_sign_up() throws Exception {
        //given
        String encryptPhoneNumber = "encryptedPhoneNumber";
        UserInfo userInfo = UserInfo.builder()
                .phoneNumber(phoneNumber)
                .build();

        when(encryptService.encryptPhoneNumber(phoneNumber)).thenReturn(encryptPhoneNumber);
        when(userRepository.save(any(UserInfo.class))).thenReturn(userInfo);

        //when
        UserInfo saveUser = userService.save(signUpRequestDto);

        //then
        assertThat(saveUser.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    @DisplayName("로그인 서비스 테스트")
    void user_sign_in() throws Exception {
        //given

        authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber,"");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtTokenDto);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(null);

        //when
        JwtTokenDto signInUserToken = userService.signIn(signInRequestDto);

        //then
        assertThat(signInUserToken).isEqualTo(jwtTokenDto);
    }

    // 토큰 재발급과 로그아웃은 redis 저장소 테스트기 때문에 repository 테스트로 진행
}
