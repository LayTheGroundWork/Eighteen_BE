package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.SchoolData;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.request.SignUpRequestDto;
import com.st.eighteen_be.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("SnsLinkService 테스트")
@Transactional
@ActiveProfiles("Test")
@SpringBootTest
class SnsLinkServiceTest {

    @Autowired
    private UserRepository userRepository;

    private SignUpRequestDto signUpRequestDto;

    private final String phoneNumber = "01012345678";

    @Autowired
    private SnsLinkService snsLinkService;

    @BeforeEach
    void setUp() {

        LocalDate birthDay = LocalDate.of(1999,12,23);
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

    @Test
    public void add_snsLink() throws Exception {
        //given
        UserInfo findUser = userRepository.save(signUpRequestDto.toEntity(phoneNumber));
        Integer testUserId = findUser.getId();

        //when
        List<String> snsLinks = new ArrayList<>();
        snsLinks.add("instagram.com");
        snsLinks.add("tiktok.com");
        snsLinkService.addSnsLink(testUserId,snsLinks);

        //then
        assertThat(findUser.getSnsLinks().get(0).getLink()).isEqualTo(snsLinks.get(0));
        assertThat(findUser.getSnsLinks().get(1).getLink()).isEqualTo(snsLinks.get(1));
    }

}