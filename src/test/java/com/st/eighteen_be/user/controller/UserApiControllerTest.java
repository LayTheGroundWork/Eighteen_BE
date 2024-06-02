package com.st.eighteen_be.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.st.eighteen_be.jwt.JwtTokenDto;
import com.st.eighteen_be.user.WithCustomMockUser;
import com.st.eighteen_be.user.domain.UserPrivacy;
import com.st.eighteen_be.user.dto.sign.SignInRequestDto;
import com.st.eighteen_be.user.dto.sign.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.st.eighteen_be.user.service
 * fileName       : UserServiceTest
 * author         : ehgur
 * date           : 24. 5. 31.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 31.        ehgur            최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserApiController 테스트")
class UserApiControllerTest {

    // 회원 가입 API URL을 상수로 정의
    private static final String SIGN_UP_URL = "/v1/api/user/sign-up";
    private static final String SIGN_IN_URL = "/v1/api/user/sign-in";
    private static final String SIGN_OUT_URL = "/v1/api/user/sign-out";
    private static final String REISSUE_URL = "/v1/api/user/reissue";

    @Autowired
    private MockMvc mockMvc; // MockMvc를 사용하여 HTTP 요청을 테스트

    @MockBean
    private UserService userService; // UserService를 Mocking

    @MockBean
    private UserRepository userRepository; // UserRepository를 Mocking

    private JwtTokenDto jwtTokenDto;

    private SignUpRequestDto signUpRequestDto; // 테스트에 사용할 SignUpRequestDto 객체

    private SignInRequestDto signInRequestDto; // 테스트에 사용할 SignInRequestDto 객체

    private ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위한 ObjectMapper 객체

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        jwtTokenDto = JwtTokenDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .accessTokenExpiresIn(60000)
                .refreshToken("refreshToken")
                .build();

        String birthday = "2018-12-15T10:10";
        String phoneNumber = "01012341234";
        String verificationCode = "123456";

        signUpRequestDto = SignUpRequestDto.builder()
                .phoneNumber(phoneNumber)
                .birthDay(LocalDateTime.parse(birthday))
                .build();

        signInRequestDto = SignInRequestDto.builder()
                .phoneNumber(phoneNumber)
                .verificationCode(verificationCode)
                .build();

        // ObjectMapper에 JavaTimeModule 등록
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("회원 가입")
    void user_sign_up() throws Exception {
        // given: 테스트에 필요한 데이터와 Mock 객체의 동작 정의
        UserPrivacy userPrivacy = signUpRequestDto.toEntity("encryptPhoneNumber");
        when(userService.save(signUpRequestDto)).thenReturn(userPrivacy);

        // when: 실제 API 호출
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        // then: 결과 검증
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("회원 로그인")
    void user_sign_in() throws Exception {
        //given
        when(userService.signIn(signInRequestDto)).thenReturn(jwtTokenDto);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(SIGN_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    @WithCustomMockUser
    @DisplayName("회원 로그아웃")
    void user_sign_out() throws Exception {
        //given
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(SIGN_OUT_URL)
                        .header("Authorization", "Bearer-abed")
                        .header("Refresh","abeda"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    @WithCustomMockUser
    @DisplayName("회원 토큰 재발급")
    void user_sign_reissue() throws Exception {
        //given
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(REISSUE_URL)
                        .header("Authorization", "Bearer-abed.sv.ds")
                        .header("Refresh","abe.da.sdf"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

    }
}