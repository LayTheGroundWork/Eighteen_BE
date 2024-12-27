package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserSearchInfoResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.repository.UserSearchInfoRedisRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * packageName    : com.st.eighteen_be.user.service
 * fileName       : UserServiceTest
 * author         : sjunpark
 * date           : 24. 12. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 24.        sjunpark       최초 생성
 */
@DisplayName("UserService 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    
    @Nested
    @Transactional
    @DisplayName("5분전에 생긴 회원 데이터 레디스 정상 업데이트 되는지 확인")
    class UpdateUserSearchInfoHashTest {
        @Autowired
        private UserService userService;
        
        @PersistenceContext
        private EntityManager em;
        
        @Autowired
        private UserSearchInfoRedisRepository userSearchInfoRedisRepository;
        
        @Test
        @DisplayName("5분전에 생성된 회원 데이터 레디스 업데이트 테스트")
        void update_user_search_info_hash_test() {
            // given
            UserInfo tester = UserInfo.builder()
                    .thumbnail("thumbnail_testKey")
                    .phoneNumber("-119 -78 -26 74 -115 14 -4 -28 -15 56 66 -128 -120 -80 -88 51")
                    .nickName("김선지해장국")
                    .birthDay(LocalDate.of(1999, 12, 24))
                    .schoolData(null)
                    .uniqueId("uniqueId_testKey")
                    .category(CategoryType.ETC)
                    .tournamentJoin(true)
                    .build();
            
            em.persist(tester);
            em.flush();
            em.clear();
            
            // when
            userService.updateUserInfoToRedis();
            
            // then
            final UserSearchInfoHash foundUser = userSearchInfoRedisRepository.findById(tester.getUniqueId()).get();
            
            assertThat(foundUser.getThumbnailUrl()).isEqualTo(tester.getThumbnail());
            assertThat(foundUser.getNickName()).isEqualTo(tester.getNickName());
            assertThat(foundUser.getUniqueId()).isEqualTo(tester.getUniqueId());
        }
        
        @Test
        @DisplayName("3분전에 생성된 유저가 수정된 경우 레디스 업데이트 테스트")
        void update_user_search_info_hash_test2() {
            // given
            //레디스에 유저 올려놓기
            final UserSearchInfoHash madeUser = UserSearchInfoHash.of("uniqueId_testKey", "thumbnail_testKey", "김선지해장국");
            userSearchInfoRedisRepository.save(madeUser);
            
            // 썸네일 임의 수정
            UserInfo tester = UserInfo.builder()
                    .thumbnail("thumbnail_testKey2")
                    .phoneNumber("-119 -78 -26 74 -115 14 -4 -28 -15 56 66 -128 -120 -80 -88 51")
                    .nickName("김선지해장국")
                    .birthDay(LocalDate.of(1999, 12, 24))
                    .schoolData(null)
                    .uniqueId("uniqueId_testKey")
                    .category(CategoryType.ETC)
                    .tournamentJoin(true)
                    .build();
            
            em.persist(tester);
            
            // when
            userService.updateUserInfoToRedis();
            
            // then
            final UserSearchInfoHash foundUser = userSearchInfoRedisRepository.findById(madeUser.getUniqueId()).get();
            
            assertThat(foundUser.getThumbnailUrl()).isEqualTo("thumbnail_testKey2");
            assertThat(foundUser.getNickName()).isEqualTo(madeUser.getNickName());
            assertThat(foundUser.getUniqueId()).isEqualTo(madeUser.getUniqueId());
        }
    }
    
    @Nested
    @DisplayName("유저 정보 조회 테스트")
    class FindUserTest {
        @PersistenceContext
        private EntityManager em;
        
        @Autowired
        private UserSearchInfoRedisRepository userSearchInfoRedisRepository;
        
        @Test
        @DisplayName("유저 정보 조회 id 로 조회하기")
        void find_user_test() {
            // given
            //레디스에 유저 올려놓기
            final UserSearchInfoHash madeUser = UserSearchInfoHash.of("uniqueId_testKey", "thumbnail_testKey", "김선지해장국");
            userSearchInfoRedisRepository.save(madeUser);
            
            // when
            final Flux<UserSearchInfoResponseDto> foundUser = userService.findAllUserSearchInfo("uniqueId");
            
            // then
            StepVerifier.create(foundUser)
                    .assertNext(user -> {
                        assertSoftly(softly -> {
                            softly.assertThat(user.getUniqueId()).isEqualTo(madeUser.getUniqueId());
                            softly.assertThat(user.getNickName()).isEqualTo(madeUser.getNickName());
                            softly.assertThat(user.getThumbnailUrl()).isEqualTo(madeUser.getThumbnailUrl());
                        });
                    })
                    .verifyComplete();
        }
        
        @Test
        @DisplayName("유저 정보 조회 닉네임으로 조회하기")
        void find_user_test2() {
            // given
            //레디스에 유저 올려놓기
            final UserSearchInfoHash madeUser = UserSearchInfoHash.of("uniqueId_testKey", "thumbnail_testKey", "김선지해장국");
            userSearchInfoRedisRepository.save(madeUser);
            
            // when
            final Flux<UserSearchInfoResponseDto> foundUser = userService.findAllUserSearchInfo("김선지");
            
            // then
            StepVerifier.create(foundUser)
                    .assertNext(user -> {
                        assertSoftly(softly -> {
                            softly.assertThat(user.getUniqueId()).isEqualTo(madeUser.getUniqueId());
                            softly.assertThat(user.getNickName()).isEqualTo(madeUser.getNickName());
                            softly.assertThat(user.getThumbnailUrl()).isEqualTo(madeUser.getThumbnailUrl());
                        });
                    })
                    .verifyComplete();
        }
    }
}
