package com.st.eighteen_be.tournament.integration;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BeanArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.st.eighteen_be.CreateTester;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.service.TournamentService;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserLike;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.st.eighteen_be.tournament.integration
 * fileName       : TournamentIntegrationTest
 * author         : ipeac
 * date           : 24. 11. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 11. 24.        ipeac       최초 생성
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("토너먼트 통합 테스트")
@ExtendWith(MockitoExtension.class)
public class TournamentIntegrationTest {
    @Nested
    @DisplayName("토너먼트 유저 선택 테스트")
    class PickUserTest {
        @PersistenceContext
        private EntityManager em;
        
        @Autowired
        private TournamentService tournamentSerivce;
        
        @Autowired
        private TournamentParticipantRepository tournamentParticipantRepository;
        
        @Autowired
        private TournamentEntityRepository tournamentEntityRepository;
        
        @MockBean
        private CreateTester createTester; // Mock으로 대체
        
        List<UserInfo> userInfos = new ArrayList<>();
        
        @BeforeEach
        void setUp() {
            FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
                    .objectIntrospector(new FailoverIntrospector(
                            Arrays.asList(
                                    FieldReflectionArbitraryIntrospector.INSTANCE,
                                    BeanArbitraryIntrospector.INSTANCE,
                                    BuilderArbitraryIntrospector.INSTANCE
                            )
                    ))
                    .defaultNotNull(true)
                    .build();
            
            //토너먼트 참가자들을 생성한다.
            for (int i = 0; i < 16; i++) {
                final UserInfo userinfo = fixtureMonkey.giveMeBuilder(UserInfo.class)
                        .setNull("id")
                        .setNull("mbti")
                        .set("nickName", Arbitraries.strings().ascii().ofLength(50)) // 고정 길이 ASCII 문자열
                        .set("uniqueId", "tester1" + (i + 1))
                        .set("introduction", Arbitraries.strings().ascii().ofLength(100)) // 고정 길이 소개
                        .setNotNull("birthDay")
                        .set("phoneNumber", "0100000000" + (i + 1))
                        .set("mediaDataList", List.of())
                        .set("userLikes", List.of())
                        .set("roles", Set.of())
                        .set("userQuestions", List.of())
                        .set("category", CategoryType.ETC)
                        .sample();
                
                final UserMediaData userMediaData = fixtureMonkey.giveMeBuilder(UserMediaData.class)
                        .setNull("id")
                        .set("imageKey", "https://my-image-bucket.s3.us-west-2.amazonaws.com/images/profile-picture.jpg")
                        .sample();
                
                UserLike.addLikedId(userinfo, 1);
                
                userMediaData.setUser(userinfo);
                
                userInfos.add(userinfo);
            }
            
            //프로필 이미지 null 인 사용자 생성
            for (int i = 0; i < 16; i++) {
                final UserInfo userinfo = fixtureMonkey.giveMeBuilder(UserInfo.class)
                        .setNull("id")
                        .setNull("mbti")
                        .set("nickName", Arbitraries.strings().ascii().ofLength(50)) // 고정 길이 ASCII 문자열
                        .set("uniqueId", "tester2" + (i + 1))
                        .set("introduction", Arbitraries.strings().ascii().ofLength(100)) // 고정 길이 소개
                        .setNotNull("birthDay")
                        .set("phoneNumber", "0100000001" + (i + 1))
                        .set("mediaDataList", List.of())
                        .set("userLikes", List.of())
                        .set("roles", Set.of())
                        .set("userQuestions", List.of())
                        .set("category", CategoryType.ETC)
                        .sample();
                
                final UserMediaData userMediaData = fixtureMonkey.giveMeBuilder(UserMediaData.class)
                        .setNull("id")
                        .setNull("imageKey")
                        .sample();
                
                UserLike.addLikedId(userinfo, 1);
                
                userMediaData.setUser(userinfo);
                
                userInfos.add(userinfo);
            }
            
            userInfos.forEach(em::persist);
        }
        
        @Test
        @DisplayName("토너먼트 유저중에 profile 이 null 인 사람이 있는 경우 선택되지 않는 지 테스트한다. 사용자가 32명정도 존재하며 16명이 profile 이 null 이다.")
        void pickUserTest() {
            // given
            
            // when
            final Set<MostLikedUserResponseDto> mostLikedUserResponseDtos = tournamentSerivce.saveMostLikedUsersToRedis();
            
            // then
            assertThat(mostLikedUserResponseDtos).hasSize(16);
            mostLikedUserResponseDtos.forEach(mostLikedUserResponseDto -> {
                assertThat(mostLikedUserResponseDto.getProfileImageUrl()).isNotNull();
            });
        }
    }
}
