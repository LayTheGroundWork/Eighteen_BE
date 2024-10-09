package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.annotation.ServiceWithMySQLTest;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.repository.RandomUserRedisRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.st.eighteen_be.tournament.domain.entity.TournamentEntity.THUMBNAIL_DEFAULT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * packageName    : com.st.eighteen_be.tournament.service
 * fileName       : TournamentServiceTest
 * author         : ipeac
 * date           : 24. 5. 19.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 19.        ipeac       최초 생성
 */
@DisplayName("TournamentService 테스트")
@ServiceWithMySQLTest
@ExtendWith(MockitoExtension.class)
class TournamentServiceMySQLTest {
    @PersistenceContext
    private EntityManager em;

    private TournamentService tournamentService;

    @Autowired
    private TournamentEntityRepository tournamentEntityRepository;

    @Autowired
    private TournamentParticipantRepository tournamentParticipantEntityRepository;

    @Autowired
    private VoteEntityRepository voteEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private RedisTemplate<String, RandomUser> redisTemplate;

    @MockBean
    private UserService userService;

    @MockBean
    private RandomUserRedisRepository randomUserRedisRepository;

    @Mock
    private ListOperations<String, RandomUser> listOperations;

    @Mock
    private HashOperations<String, String, RandomUser> hashOperations;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(userService, tournamentEntityRepository, tournamentParticipantEntityRepository, voteEntityRepository, userRepository, randomUserRedisRepository,
                redisTemplate);

        given(redisTemplate.opsForList()).willReturn(listOperations);
        given(redisTemplate.opsForHash()).willAnswer(invocation -> hashOperations);
    }

    @Test
    @DisplayName("분야별 토너먼트 정상 조회 테스트")
    void When_searchTournamentByCategory_Then_returnTournamentList() {
        // given
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .category(CategoryType.GAME)
                                                    .build();

        TournamentEntity tournamentEntity2 = TournamentEntity.builder()
                .category(CategoryType.SPORT)
                                                     .build();

        tournamentEntityRepository.saveAll(List.of(tournamentEntity, tournamentEntity2));

        // when
        List<TournamentSearchResponseDTO> actual = tournamentService.search(PageRequest.of(0, 2), CategoryType.GAME);

        // then
        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getTournamentThumbnailUrl()).isEqualTo(THUMBNAIL_DEFAULT_URL);
    }

    @Test
    @DisplayName("분야별 토너먼트 조회 시 해당하는 토너먼트가 없는 경우 빈 리스트 반환 테스트")
    void When_searchTournamentByCategory_Then_returnEmptyList() {
        // given
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .category(CategoryType.GAME)
                                                    .build();

        TournamentEntity tournamentEntity2 = TournamentEntity.builder()
                .category(CategoryType.SPORT)
                                                     .build();

        tournamentEntityRepository.saveAll(List.of(tournamentEntity, tournamentEntity2));

        // when
        List<TournamentSearchResponseDTO> actual = tournamentService.search(PageRequest.of(0, 2), CategoryType.ETC);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("토너먼트를 시작합니다. 카테고리에 맞는 토너먼트 생성을 확인한다.")
    void When_startTournament_Then_createTournament() {
        // given
        List<TournamentParticipantEntity> tournamentParticipantEntities = List.of(
                TournamentParticipantEntity.of("user1"),
                TournamentParticipantEntity.of("user2"),
                TournamentParticipantEntity.of("user3"),
                TournamentParticipantEntity.of("user4"),
                TournamentParticipantEntity.of("user5"),
                TournamentParticipantEntity.of("user6"),
                TournamentParticipantEntity.of("user7"),
                TournamentParticipantEntity.of("user8")
        );

        //redisTemplate.opsForList() 모킹
        List<RandomUser> randomUsers = IntStream.range(0, 16)
                .boxed()
                .map(i -> RandomUser.of("qkrtkdwns3410", "http://test.com", "예술"))
                .collect(Collectors.toList());

        //hashOperations.values(categoryKey). 에 대한 모킹처리
        given(redisTemplate.opsForHash()).willAnswer(invocation -> hashOperations);
        given(hashOperations.values(anyString())).willReturn(randomUsers);

        // when
        tournamentService.startTournament();

        // then
        List<TournamentEntity> actual = tournamentEntityRepository.findAll();

        assertThat(actual).isNotEmpty().hasSize(CategoryType.values().length);
    }

    @Nested
    @DisplayName("토너먼트 시작 테스트")
    class StartTournamentTest {
        @Test
        @DisplayName("토너먼트 시작시 토너먼트가 생성되는지 확인한다.")
        void When_startTournament_Then_createTournament() {
            // when
            tournamentService.startTournament();

            // then
            List<TournamentEntity> foundAll = tournamentEntityRepository.findAll();

            assertThat(foundAll)
                    .isNotEmpty()
                    .hasSize(CategoryType.values().length);
        }

        @Test
        @DisplayName("토너먼트 시작시 토너먼트 참가자가 랜덤으로 선정되는지 확인한다.")
        void When_startTournament_Then_pickRandomUser() {
            // given
            final int savedParticipantCount = 32;
            final int expectedCount = savedParticipantCount * CategoryType.values().length;

            //토너먼트 참여자 선정 모킹 데이터 given
            List<RandomUser> randomUsers = IntStream.range(0, savedParticipantCount)
                    .boxed()
                    .map(i -> RandomUser.of("userId" + i, "http://test.com", "예술"))
                    .toList();

            //hashOperations.values(categoryKey). 에 대한 모킹처리
            given(redisTemplate.opsForHash()).willAnswer(invocation -> hashOperations);
            given(hashOperations.values(anyString())).willReturn(randomUsers);

            // when
            tournamentService.startTournament();

            // then
            List<TournamentParticipantEntity> foundAll = tournamentParticipantEntityRepository.findAll();

            assertThat(foundAll)
                    .isNotEmpty()
                    .hasSize(expectedCount);
        }

        @Test
        @DisplayName("종료된 토너먼트가 있는 경우 시즌이 증가되어 새로운 토너먼트가 생성되는지 확인한다.")
        void When_startTournament_Then_createNewTournament() {
            // given
            List<TournamentEntity> tournamentEntities = new ArrayList<>();

            for (CategoryType category : CategoryType.values()) {
                TournamentEntity gameTournament = TournamentEntity.builder()
                                                          .season(2)
                                                          .status(false)
                                                          .category(category)
                                                          .build();

                tournamentEntities.add(gameTournament);
            }

            tournamentEntityRepository.saveAll(tournamentEntities);

            // when
            tournamentService.startTournament();

            // then
            List<TournamentEntity> activeTournaments = em.createQuery(
                    "SELECT t FROM TournamentEntity t WHERE t.status = true"
                    , TournamentEntity.class
            ).getResultList();

            assertThat(activeTournaments)
                    .isNotEmpty()
                    .hasSize(CategoryType.values().length);

            assertSoftly(
                    softly -> {
                        for (TournamentEntity tournamentEntity : activeTournaments) {
                            softly.assertThat(tournamentEntity.getSeason()).isEqualTo(3);
                        }
                    }
            );
        }
    }

    @Nested
    @DisplayName("토너먼트 종료 테스트")
    class EndTournamentTest {
        @Test
        @DisplayName("토너먼트 종료시 마지막 차시의 토너먼트의 상태값이 변경되는지 확인한다.")
        void When_endTournament_Then_changeTournamentStatus() {
            // given
            List<TournamentEntity> tournamentEntities = new ArrayList<>();

            for (CategoryType category : CategoryType.values()) {
                TournamentEntity gameTournament = TournamentEntity.builder()
                                                          .category(category)
                                                          .build();

                tournamentEntities.add(gameTournament);
            }

            tournamentEntityRepository.saveAll(tournamentEntities);

            // when
            tournamentService.endLastestTournaments();

            // then
            List<TournamentEntity> foundAll = tournamentEntityRepository.findAll();

            assertThat(foundAll)
                    .isNotEmpty()
                    .allMatch(tournamentEntity -> Objects.equals(tournamentEntity.isStatus(), Boolean.FALSE), "토너먼트 상태값이 변경되지 않았습니다.");
        }

        @Test
        @DisplayName("토너먼트 종료시 토너먼트 투표순으로 승자를 선정한다.")
        void When_endTournament_Then_determineWinner() {
            //TODO 프로필 설정완료되면 관련 프로필까지 조회되는지 테스트 추가해야합니다.. 2024-06-09
            // given
            //참여자 정보 주입하기
            for (int i = 1; i <= 3; i++) {
                UserInfo user = UserInfo.builder()
                                        .birthDay(LocalDate.now())
                                        .phoneNumber("010-1234-567" + i)
                                        .uniqueId("user" + i)
                                        .nickName("name" + i)
                        .category(CategoryType.ART)
                                        .build();

                em.persist(user);
            }

            TournamentEntity tournamentEntity = TournamentEntity.builder()
                    .category(CategoryType.GAME)
                                                        .build();

            tournamentEntityRepository.save(tournamentEntity);

            //참여자 추가하기
            List<TournamentParticipantEntity> tournamentParticipantEntities = List.of(
                    TournamentParticipantEntity.of("user1", tournamentEntity),
                    TournamentParticipantEntity.of("user2", tournamentEntity)
            );

            //투표자들 추가하기
            Result result = getResult(tournamentEntity, tournamentParticipantEntities);

            //점수 직접 추가
            ReflectionTestUtils.setField(tournamentParticipantEntities.get(0), "score", result.voter1().getVotePoint() + result.voter2().getVotePoint());
            ReflectionTestUtils.setField(tournamentParticipantEntities.get(1), "score", result.voter3().getVotePoint());

            tournamentParticipantEntityRepository.saveAll((tournamentParticipantEntities));

            voteEntityRepository.saveAll(List.of(result.voter1(), result.voter2(), result.voter3()));

            // when
            List<TournamentVoteResultResponseDTO> actual = tournamentService.determineWinner(tournamentEntity.getTournamentNo());

            // then
            assertThat(actual).isNotEmpty();
            assertThat(actual.size()).isEqualTo(2);

            assertSoftly(
                    softly -> {
                        softly.assertThat(actual.get(0).getRankerId()).isEqualTo("user2");
                        softly.assertThat(actual.get(0).getRank()).isEqualTo(1);
                        softly.assertThat(actual.get(0).getVoteCount()).isEqualTo(3);
                        //softly.assertThat(actual.get(0).getProfileImageUrl()).isEqualTo("https://picsum.photos/200");

                        softly.assertThat(actual.get(1).getRankerId()).isEqualTo("user1");
                        softly.assertThat(actual.get(1).getRank()).isEqualTo(2);
                        softly.assertThat(actual.get(1).getVoteCount()).isEqualTo(2);
                        //softly.assertThat(actual.get(1).getProfileImageUrl()).isEqualTo("https://picsum.photos/200");
                    }
            );
        }

        private static @NotNull Result getResult(TournamentEntity tournamentEntity, List<TournamentParticipantEntity> tournamentParticipantEntities) {
            VoteEntity voter1 = VoteEntity.builder()
                                        .tournament(tournamentEntity)
                                        .participant(tournamentParticipantEntities.get(0))
                    .voterId("user1")
                                        .votePoint(1)
                                        .build();

            VoteEntity voter2 = VoteEntity.builder()
                                        .tournament(tournamentEntity)
                                        .participant(tournamentParticipantEntities.get(0))
                    .voterId("user2")
                                        .votePoint(1)
                                        .build();

            VoteEntity voter3 = VoteEntity.builder()
                                        .tournament(tournamentEntity)
                                        .participant(tournamentParticipantEntities.get(1))
                    .voterId("user3")
                                        .votePoint(3)
                                        .build();

            return new Result(voter1, voter2, voter3);
        }

        private record Result(VoteEntity voter1, VoteEntity voter2, VoteEntity voter3) {
        }
    }

    @Nested
    @DisplayName("토너먼트 투표 테스트")
    class VoteTournamentTest {
        @Test
        @DisplayName("토너먼트 투표시 투표가 정상적으로 처리되는지 확인한다")
        void When_voteTournament_Then_processVote() {
            // given
            TournamentEntity tournamentEntity = TournamentEntity.builder()
                    .category(CategoryType.GAME)
                                                        .build();

            tournamentEntityRepository.save(tournamentEntity);

            TournamentParticipantTestResult result = getTournamentParticipantTestResult(tournamentEntity);

            tournamentParticipantEntityRepository.saveAll(result.participantEntities);

            TournamentVoteRequestDTO tournamentVoteRequestDTO = TournamentVoteRequestDTO.builder()
                    .tournamentNo(tournamentEntity.getTournamentNo())
                    .participantIdsOrderByRank(result.participantEntities.stream().map(TournamentParticipantEntity::getUserId).collect(Collectors.toList()))
                    .build();

            given(userService.findByToken(anyString())).willReturn(UserInfo.builder().uniqueId("testUser1").build());

            // when
            tournamentService.processVote(tournamentVoteRequestDTO, "accessToken");

            // then
            List<TournamentParticipantEntity> found = tournamentParticipantEntityRepository.findAll();

            assertThat(found)
                    .isNotEmpty();

            assertSoftly(softAssertions -> {
                softAssertions.assertThat(found.get(0).getScore()).isEqualTo(4);
                softAssertions.assertThat(found.get(1).getScore()).isEqualTo(2);
                softAssertions.assertThat(found.get(2).getScore()).isEqualTo(1);
                softAssertions.assertThat(found.get(3).getScore()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("토너먼트 투표시 투표기록이 정상적으로 처리되는지 확인한다")
        void When_voteTournament_Then_insertVoteRecord() {
            // given
            TournamentEntity tournamentEntity = TournamentEntity.builder()
                    .category(CategoryType.GAME)
                                                        .build();

            tournamentEntityRepository.save(tournamentEntity);

            TournamentParticipantTestResult result = getTournamentParticipantTestResult(tournamentEntity);

            tournamentParticipantEntityRepository.saveAll(result.participantEntities);

            TournamentVoteRequestDTO tournamentVoteRequestDTO =
                    TournamentVoteRequestDTO.builder()
                            .tournamentNo(tournamentEntity.getTournamentNo())
                            .participantIdsOrderByRank(result.participantEntities.stream().map(TournamentParticipantEntity::getUserId).collect(Collectors.toList()))
                            .build();


            given(userService.findByToken(anyString())).willReturn(UserInfo.builder().uniqueId("testUser1").build());

            // when
            tournamentService.processVote(tournamentVoteRequestDTO, "accessToken");

            // then
            List<VoteEntity> found = voteEntityRepository.findAll();

            assertThat(found)
                    .isNotEmpty()
                    .hasSize(32);
        }

        private static @NotNull TournamentParticipantTestResult getTournamentParticipantTestResult(TournamentEntity tournamentEntity) {
            List<TournamentParticipantEntity> participants = IntStream.rangeClosed(1, 32)
                    .mapToObj(i -> TournamentParticipantEntity.of("user" + i, tournamentEntity))
                    .toList();

            return new TournamentParticipantTestResult(participants);
        }

        private record TournamentParticipantTestResult(List<TournamentParticipantEntity> participantEntities) {

        }
    }

    @Nested
    @DisplayName("토너먼트 참가자 선정 테스트")
    class PickRandomUserTest {
        @Test
        @Disabled("유저 부족한 케이스에 대해 다시 생각해보기")
        @DisplayName("랜덤 유저 선정시 유저가 32명 미만인 경우 예외를 발생시킨다.")
        void When_pickRandomUser_Then_throwException() {
            // given
            List<UserInfo> userInfos = new ArrayList<>();

            for (int i = 1; i <= 2; i++) {
                UserInfo user = UserInfo.builder()
                        .birthDay(LocalDate.now())
                        .phoneNumber("010-1234-567" + i)
                        .uniqueId("user" + i)
                        .nickName("name" + i)
                        .category(CategoryType.ART)
                        .build();

                userInfos.add(user);
            }

            userRepository.saveAll(userInfos);

            // when
            // then
            assertThatThrownBy(() -> tournamentService.saveRandomUser())
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("유저 수가 부족합니다");
        }

        @Test
        @DisplayName("랜덤 유저 선정시 유저가 32명 이상인 경우 랜덤 유저를 선정한다. - Redis 테스트")
        void When_pickRandomUser_Then_returnRandomUser() {
            // given
            final int pickedUserCount = 32;

            List<UserInfo> userInfos = new ArrayList<>();

            for (int i = 1; i <= pickedUserCount; i++) {
                UserInfo user = UserInfo.builder()
                        .birthDay(LocalDate.now())
                        .phoneNumber("010-1234-567" + i)
                        .uniqueId("user" + i)
                        .nickName("name" + i)
                        .tournamentJoin(true) // 토너먼트 참여 여부
                        .category(CategoryType.ART)
                        .build();

                userInfos.add(user);
            }

            userRepository.saveAll(userInfos);

            // when
            Set<UserRandomResponseDto> actual = tournamentService.saveRandomUser();

            // then
            assertThat(actual)
                    .isNotEmpty()
                    .hasSize(pickedUserCount);
        }
    }
}
