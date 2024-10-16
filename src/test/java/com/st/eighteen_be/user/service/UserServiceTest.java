//package com.st.eighteen_be.user.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.st.eighteen_be.jwt.JwtTokenDto;
//import com.st.eighteen_be.jwt.JwtTokenProvider;
//import com.st.eighteen_be.token.domain.RefreshToken;
//import com.st.eighteen_be.token.repository.RefreshTokenRepository;
//import com.st.eighteen_be.user.WithCustomMockUser;
//import com.st.eighteen_be.user.domain.SchoolData;
//import com.st.eighteen_be.user.domain.UserInfo;
//import com.st.eighteen_be.user.dto.request.SignInRequestDto;
//import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
//import com.st.eighteen_be.user.dto.response.SignUpResponseDto;
//import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
//import com.st.eighteen_be.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@DisplayName("UserService 테스트")
//@ActiveProfiles("Test")
//@SpringBootTest
//public class UserServiceTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private LikeService likeService;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private UsernamePasswordAuthenticationToken authenticationToken;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockBean
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @MockBean
//    private EncryptService encryptService;
//
//    private SignUpRequestDto signUpRequestDto;
//
//    private SignInRequestDto signInRequestDto;
//
//    private ObjectMapper objectMapper;
//
//    private JwtTokenDto jwtTokenDto;
//
//    private final String phoneNumber = "01012341234";
//    private final String uniqueId = "@abc_sc";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        LocalDate birthDay = LocalDate.of(1999,12,23);
//        String verificationCode = "123456";
//        String nickName = "ehgur";
//        String schoolName = "서울고등학교";
//        String schoolLocation = "서울";
//
//        SchoolData schoolData = new SchoolData(schoolName,schoolLocation);
//
//        signUpRequestDto = SignUpRequestDto.builder()
//                .phoneNumber(phoneNumber)
//                .birthDay(birthDay)
//                .schoolData(schoolData)
//                .uniqueId(uniqueId)
//                .nickName(nickName)
//                .build();
//
//        signInRequestDto = SignInRequestDto.builder()
//                .phoneNumber(phoneNumber)
//                .verificationCode(verificationCode)
//                .build();
//
//        jwtTokenDto = JwtTokenDto.builder()
//                .grantType("Bearer")
//                .accessToken("accessToken")
//                .accessTokenExpiresIn(1000)
//                .refreshToken("refreshToken")
//                .build();
//
//        // ObjectMapper에 JavaTimeModule 등록
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//    }
//
//    @Test
//    @DisplayName("회원가입 서비스 테스트")
//    void user_sign_up() throws Exception {
//        //given
//        String encryptPhoneNumber = "encryptedPhoneNumber";
//        UserInfo userInfo = UserInfo.builder()
//                .uniqueId(uniqueId)
//                .schoolData(new SchoolData("서울고등학교", "서울"))
//                .birthDay(LocalDate.of(1999,12,23))
//                .nickName("chd")
//                .phoneNumber(phoneNumber)
//                .build();
//
//        Set<String> roles = new HashSet<>();
//        roles.add("USER");
//
//        when(userService.save(signUpRequestDto)).thenReturn(new SignUpResponseDto(userInfo,roles));
//        when(userRepository.save(any())).thenReturn(userInfo);
//        when(encryptService.encryptPhoneNumber(phoneNumber)).thenReturn(encryptPhoneNumber);
//
//        //when
//        SignUpResponseDto saveUser = userService.save(signUpRequestDto);
//
//        //then
//        assertThat(saveUser.getUniqueId()).isEqualTo(uniqueId);
//    }
//
//    @Test
//    @DisplayName("로그인 서비스 테스트")
//    void user_sign_in() throws Exception {
//        //given
//        UserInfo userInfo = signUpRequestDto.toEntity(phoneNumber);
//
//        when(encryptService.encryptPhoneNumber(phoneNumber)).thenReturn(phoneNumber);
//        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.ofNullable(userInfo));
//
//        authenticationToken = new UsernamePasswordAuthenticationToken(uniqueId,"");
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authenticationToken);
//
//        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtTokenDto);
//        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(null);
//
//        //when
//        JwtTokenDto signInUserToken = userService.signIn(signInRequestDto);
//
//        //then
//        assertThat(signInUserToken).isEqualTo(jwtTokenDto);
//    }
//
////    @Test
////    @WithCustomMockUser
////    @DisplayName("유저 좋아요 추가")
////    public void addLike() throws Exception {
////        //given
////        Integer likedId = 1;
////
////        UserInfo mockUser = UserInfo.builder()
////                .phoneNumber(phoneNumber)
////                .uniqueId(uniqueId)
////                .nickName("nickName")
////                .build();
////
////        UsernamePasswordAuthenticationToken mockAuth= new UsernamePasswordAuthenticationToken(uniqueId,"");
////
////        String token = "Bearer abc.sdf.sdf";
////
////        when(userRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(mockUser));
////        when(jwtTokenProvider.resolveAccessToken(any())).thenReturn(token);
////        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
////        when(jwtTokenProvider.getAuthentication(any())).thenReturn(mockAuth);
////
////        //when
////        likeService.addLike(token, likedId);
////
////        //then
////        assertThat(likeService.countLikes(likedId)).isEqualTo(1);
////        assertThat(likeService.getLikedUserId(token,likedId)).isTrue();
////    }
////
////    @Test
////    @WithCustomMockUser
////    @DisplayName("유저 좋아요 취소")
////    public void cancelLike() throws Exception {
////        //given
////        Integer likedId = 1;
////
////        UserInfo mockUser = UserInfo.builder()
////                .phoneNumber(phoneNumber)
////                .uniqueId(uniqueId)
////                .nickName("nickName")
////                .build();
////
////        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
////        UsernamePasswordAuthenticationToken mockAuth= new UsernamePasswordAuthenticationToken(uniqueId,"");
////
////        String token = "abc.sdf.sdf";
////        mockRequest.addHeader("Authorization","Bearer " + token);
////        mockRequest.addHeader("Refresh",token);
////
////        when(userRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(mockUser));
////        when(jwtTokenProvider.resolveAccessToken(any())).thenReturn(token);
////        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
////        when(jwtTokenProvider.getAuthentication(any())).thenReturn(mockAuth);
////
////        //when
////        likeService.cancelLike(token, likedId);
////
////        //then
////        assertThat(likeService.countLikes(likedId)).isEqualTo(0);
////        assertThat(likeService.getLikedUserId(token,likedId)).isFalse();
////    }
//
//    @Test
//    @WithCustomMockUser
//    @DisplayName("유저 좋아요 목록 조회")
//    public void getLikeCount() throws Exception {
//        //given
//        Integer likedId_A = 1;
//        Integer likedId_B = 2;
//
//        UserInfo mockUser = UserInfo.builder()
//                .phoneNumber(phoneNumber)
//                .uniqueId(uniqueId)
//                .nickName("nickName")
//                .build();
//
//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//        UsernamePasswordAuthenticationToken mockAuth= new UsernamePasswordAuthenticationToken(uniqueId,"");
//
//        String token = "abc.sdf.sdf";
//        mockRequest.addHeader("Authorization","Bearer " + token);
//        mockRequest.addHeader("Refresh",token);
//
//        when(userRepository.findByUniqueId(uniqueId)).thenReturn(Optional.of(mockUser));
//        when(jwtTokenProvider.resolveAccessToken(any())).thenReturn(token);
//        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
//        when(jwtTokenProvider.getAuthentication(any())).thenReturn(mockAuth);
//
//        //when
//        likeService.addLike(token,likedId_A);
//        likeService.addLike(token,likedId_B);
//
//        Set<String> likes = likeService.getLikedUserIds(token);
//
//        //then
//        assertThat(likes.size()).isEqualTo(2);
//        assertThat(likes.contains(likedId_A.toString())).isTrue();
//        assertThat(likes.contains(likedId_B.toString())).isTrue();
//
//    }
//}
