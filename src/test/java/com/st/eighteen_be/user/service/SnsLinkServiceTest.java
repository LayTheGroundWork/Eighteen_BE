package com.st.eighteen_be.user.service;

import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.WithCustomMockUser;
import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("SnsLinkService 테스트")
@Transactional
@ActiveProfiles("Test")
@SpringBootTest
class SnsLinkServiceTest {

    @Autowired
    private UserRepository userRepository;

    private SignUpRequestDto signUpRequestDto;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @InjectMocks
    private SnsLinkService snsLinkService;

    @Autowired
    private EncryptService encryptService;

    private final String phoneNumber = "01012341234";

    @BeforeEach
    void setUp() {

        LocalDate birthDay = LocalDate.of(1999, 12, 23);
        String uniqueId = "@abc_sc";
        String nickName = "ehgur";
        String schoolName = "서울고등학교";
        String schoolLocation = "서울";

        SchoolData schoolData = SchoolData.builder()
                .schoolName(schoolName)
                .schoolLocation(schoolLocation)
                .build();

        signUpRequestDto = SignUpRequestDto.builder()
                .phoneNumber(phoneNumber)
                .birthDay(birthDay)
                .schoolData(schoolData)
                .uniqueId(uniqueId)
                .nickName(nickName)
                .build();

        userRepository.deleteAll();
    }

//    @Test
//    public void add_snsLink() throws Exception {
//        // given
//        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
//
//        String accessToken = "eyJhbGciOiJIUzM4NCJ9" +
//                ".eyJzdWIiOiI1OSA3IDczIDkzIDIzIDMwIC0zIDgyIC0zMyAtMTE0IC02NSAxMiAtNjIgLTU2IDY2IDEzICIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNzE4NjkzNzE1fQ" +
//                ".tR6ogyXMBfugKZT_f3nPr38KBHFMKX54ZO0BY5RkZIZF-W1tS31Oa8fDtogldQx1";
//
//        mockRequest.addHeader("Authorization", "Bearer " + accessToken);
//
//        String encryptPhone = encryptService.encryptPhoneNumber(phoneNumber);
//        UserInfo savedUser = userRepository.save(signUpRequestDto.toEntity(encryptPhone));
//
//        UsernamePasswordAuthenticationToken authenticationToken
//                = new UsernamePasswordAuthenticationToken(phoneNumber, "");
//
//        when(jwtTokenProvider.resolveAccessToken(mockRequest)).thenReturn(accessToken);
//        when(jwtTokenProvider.getAuthentication(accessToken)).thenReturn(authenticationToken);
//
//
//        // when
//        List<String> snsLinks = new ArrayList<>();
//        snsLinks.add("instagram.com");
//        snsLinks.add("tiktok.com");
//        snsLinkService.addSnsLink(mockRequest, snsLinks);
//
//        // then
//        UserInfo findUser = userRepository.findById(savedUser.getId()).orElseThrow();
//        assertThat(findUser.getSnsLinks().get(0).getLink()).isEqualTo(snsLinks.get(0));
//        assertThat(findUser.getSnsLinks().get(1).getLink()).isEqualTo(snsLinks.get(1));
//    }
}
