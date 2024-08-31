package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.annotation.ServiceWithMySQLTest;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.domain.dto.request.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import static com.st.eighteen_be.tournament.domain.entity.TournamentEntity.THUMBNAIL_DEFAULT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

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

    @Mock
    private ListOperations<String, RandomUser> listOperations;

    @Mock
    private HashOperations<String, String, RandomUser> hashOperations;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(tournamentEntityRepository, tournamentParticipantEntityRepository, voteEntityRepository, userRepository, redisTemplate);

        given(redisTemplate.opsForList()).willReturn(listOperations);
    }

    @Test
    @DisplayName("분야별 토너먼트 정상 조회 테스트")
    void When_searchTournamentByCategory_Then_returnTournamentList() {
        // given
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                                                    .category(TournamentCategoryEnums.GAME)
                                                    .build();

        TournamentEntity tournamentEntity2 = TournamentEntity.builder()
                                                     .category(TournamentCategoryEnums.MOVIE)
                                                     .build();

        tournamentEntityRepository.saveAll(List.of(tournamentEntity, tournamentEntity2));

        // when
        List<TournamentSearchResponseDTO> actual = tournamentService.search(PageRequest.of(0, 2), TournamentCategoryEnums.GAME);

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
                                                    .category(TournamentCategoryEnums.GAME)
                                                    .build();

        TournamentEntity tournamentEntity2 = TournamentEntity.builder()
                                                     .category(TournamentCategoryEnums.MOVIE)
                                                     .build();

        tournamentEntityRepository.saveAll(List.of(tournamentEntity, tournamentEntity2));

        // when
        List<TournamentSearchResponseDTO> actual = tournamentService.search(PageRequest.of(0, 2), TournamentCategoryEnums.MUSIC);

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
        when(redisTemplate.opsForList().range(anyString(), anyLong(), anyLong())).thenReturn(List.of(
                RandomUser.builder().uid("user1").build(),
                RandomUser.builder().uid("user2").build(),
                RandomUser.builder().uid("user3").build(),
                RandomUser.builder().uid("user4").build(),
                RandomUser.builder().uid("user5").build(),
                RandomUser.builder().uid("user6").build(),
                RandomUser.builder().uid("user7").build(),
                RandomUser.builder().uid("user8").build()
        ));

        // when
        tournamentService.startTournament();

        // then
        List<TournamentEntity> actual = tournamentEntityRepository.findAll();

        assertThat(actual).isNotEmpty().hasSize(TournamentCategoryEnums.values().length);
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
                    .hasSize(TournamentCategoryEnums.values().length);
        }

        @Test
        @DisplayName("토너먼트 시작시 토너먼트 참가자가 랜덤으로 선정되는지 확인한다.")
        void When_startTournament_Then_pickRandomUser() {
            // given
            final int savedParticipantCount = 8 * TournamentCategoryEnums.values().length;

            TournamentEntity tournament = TournamentEntity.builder()
                                                  .category(TournamentCategoryEnums.GAME)
                                                  .season(1)
                                                  .build();

            em.persist(tournament);

            //토너먼트 참여자 선정 모킹 데이터 given
            given(redisTemplate.opsForList().range("pickedRandomUser", 0, 15)).willReturn(List.of(
                    RandomUser.builder().uid("user1").build(),
                    RandomUser.builder().uid("user2").build(),
                    RandomUser.builder().uid("user3").build(),
                    RandomUser.builder().uid("user4").build(),
                    RandomUser.builder().uid("user5").build(),
                    RandomUser.builder().uid("user6").build(),
                    RandomUser.builder().uid("user7").build(),
                    RandomUser.builder().uid("user8").build()
            ));

            // when
            tournamentService.startTournament();

            // then
            List<TournamentParticipantEntity> foundAll = tournamentParticipantEntityRepository.findAll();

            assertThat(foundAll)
                    .isNotEmpty()
                    .hasSize( savedParticipantCount);
        }

        @Test
        @DisplayName("종료된 토너먼트가 있는 경우 시즌이 증가되어 새로운 토너먼트가 생성되는지 확인한다.")
        void When_startTournament_Then_createNewTournament() {
            // given
            List<TournamentEntity> tournamentEntities = new ArrayList<>();

            for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
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
                    .hasSize(TournamentCategoryEnums.values().length);

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

            for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
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
                                        .build();

                em.persist(user);
            }

            TournamentEntity tournamentEntity = TournamentEntity.builder()
                                                        .category(TournamentCategoryEnums.GAME)
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
                                        .voterId("voter1")
                                        .votePoint(1)
                                        .build();

            VoteEntity voter2 = VoteEntity.builder()
                                        .tournament(tournamentEntity)
                                        .participant(tournamentParticipantEntities.get(0))
                                        .voterId("voter2")
                                        .votePoint(1)
                                        .build();

            VoteEntity voter3 = VoteEntity.builder()
                                        .tournament(tournamentEntity)
                                        .participant(tournamentParticipantEntities.get(1))
                                        .voterId("voter3")
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
                                                        .category(TournamentCategoryEnums.GAME)
                                                        .build();

            tournamentEntityRepository.save(tournamentEntity);

            TournamentParticipantTestResult result = getTournamentParticipantTestResult(tournamentEntity);

            tournamentParticipantEntityRepository.saveAll(List.of(result.user1(), result.user2(), result.user3(), result.user4(), result.user5(), result.user6(), result.user7(), result.user8(), result.user9(), result.user10(), result.user11(), result.user12(), result.user13(), result.user14(), result.user15(), result.user16()));

            TournamentVoteRequestDTO tournamentVoteRequestDTO = TournamentVoteRequestDTO.builder()
                                                                        .tournamentNo(tournamentEntity.getTournamentNo())
                                                                        .voterId("voter")
                                                                        .participantIdsOrderByRank(List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9", "user10", "user11", "user12", "user13", "user14", "user15", "user16"))
                                                                        .build();

            // when
            tournamentService.processVote(tournamentVoteRequestDTO);

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
                                                        .category(TournamentCategoryEnums.GAME)
                                                        .build();

            tournamentEntityRepository.save(tournamentEntity);

            TournamentParticipantTestResult result = getTournamentParticipantTestResult(tournamentEntity);

            tournamentParticipantEntityRepository.saveAll(List.of(result.user1(), result.user2(), result.user3(), result.user4(), result.user5(), result.user6(), result.user7(), result.user8(), result.user9(), result.user10(), result.user11(), result.user12(), result.user13(), result.user14(), result.user15(), result.user16()));

            TournamentVoteRequestDTO tournamentVoteRequestDTO =
                    TournamentVoteRequestDTO.builder()
                            .tournamentNo(tournamentEntity.getTournamentNo())
                            .voterId("voter")
                            .participantIdsOrderByRank(List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9", "user10", "user11", "user12", "user13", "user14", "user15", "user16"))
                            .build();

            // when
            tournamentService.processVote(tournamentVoteRequestDTO);

            // then
            List<VoteEntity> found = voteEntityRepository.findAll();

            assertThat(found)
                    .isNotEmpty()
                    .hasSize(16);
        }

        private static @NotNull TournamentParticipantTestResult getTournamentParticipantTestResult(TournamentEntity tournamentEntity) {

            TournamentParticipantEntity user1 = TournamentParticipantEntity.of("user1", tournamentEntity);
            TournamentParticipantEntity user2 = TournamentParticipantEntity.of("user2", tournamentEntity);
            TournamentParticipantEntity user3 = TournamentParticipantEntity.of("user3", tournamentEntity);
            TournamentParticipantEntity user4 = TournamentParticipantEntity.of("user4", tournamentEntity);
            TournamentParticipantEntity user5 = TournamentParticipantEntity.of("user5", tournamentEntity);
            TournamentParticipantEntity user6 = TournamentParticipantEntity.of("user6", tournamentEntity);
            TournamentParticipantEntity user7 = TournamentParticipantEntity.of("user7", tournamentEntity);
            TournamentParticipantEntity user8 = TournamentParticipantEntity.of("user8", tournamentEntity);
            TournamentParticipantEntity user9 = TournamentParticipantEntity.of("user9", tournamentEntity);
            TournamentParticipantEntity user10 = TournamentParticipantEntity.of("user10", tournamentEntity);
            TournamentParticipantEntity user11 = TournamentParticipantEntity.of("user11", tournamentEntity);
            TournamentParticipantEntity user12 = TournamentParticipantEntity.of("user12", tournamentEntity);
            TournamentParticipantEntity user13 = TournamentParticipantEntity.of("user13", tournamentEntity);
            TournamentParticipantEntity user14 = TournamentParticipantEntity.of("user14", tournamentEntity);
            TournamentParticipantEntity user15 = TournamentParticipantEntity.of("user15", tournamentEntity);
            TournamentParticipantEntity user16 = TournamentParticipantEntity.of("user16", tournamentEntity);

            return new TournamentParticipantTestResult(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, user14, user15, user16);
        }

        private record TournamentParticipantTestResult(TournamentParticipantEntity user1,
                                                       TournamentParticipantEntity user2,
                                                       TournamentParticipantEntity user3,
                                                       TournamentParticipantEntity user4,
                                                       TournamentParticipantEntity user5,
                                                       TournamentParticipantEntity user6,
                                                       TournamentParticipantEntity user7,
                                                       TournamentParticipantEntity user8,
                                                       TournamentParticipantEntity user9,
                                                       TournamentParticipantEntity user10,
                                                       TournamentParticipantEntity user11,
                                                       TournamentParticipantEntity user12,
                                                       TournamentParticipantEntity user13,
                                                       TournamentParticipantEntity user14,
                                                       TournamentParticipantEntity user15,
                                                       TournamentParticipantEntity user16) {
        }
    }

    @Nested
    @DisplayName("토너먼트 참가자 선정 테스트")
    class PickRandomUserTest {
        @Test
        @DisplayName("랜덤 유저 선정시 유저가 16명 미만인 경우 예외를 발생시킨다.")
        void When_pickRandomUser_Then_throwException() {
            // given
            List<UserInfo> userInfos = new ArrayList<>();

            for (int i = 1; i <= 15; i++) {
                UserInfo user = UserInfo.builder()
                        .birthDay(LocalDate.now())
                        .phoneNumber("010-1234-567" + i)
                        .uniqueId("user" + i)
                        .nickName("name" + i)
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
        @DisplayName("랜덤 유저 선정시 유저가 16명 이상인 경우 랜덤 유저를 선정한다. - Redis 테스트")
        void When_pickRandomUser_Then_returnRandomUser() {
            // given
            given(redisTemplate.opsForHash()).willAnswer(invocation -> hashOperations);

            List<UserInfo> userInfos = new ArrayList<>();

            for (int i = 1; i <= 16; i++) {
                UserInfo user = UserInfo.builder()
                        .birthDay(LocalDate.now())
                        .phoneNumber("010-1234-567" + i)
                        .uniqueId("user" + i)
                        .nickName("name" + i)
                        .build();

                userInfos.add(user);
            }

            userRepository.saveAll(userInfos);

            // when
            List<UserRandomResponseDto> actual = tournamentService.saveRandomUser();

            // then
            assertThat(actual)
                    .isNotEmpty()
                    .hasSize(16);
        }
    }
}
