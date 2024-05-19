package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.annotation.ServiceWithMySQLTest;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.GameEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.GameEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.service.helper.TournamentHelperService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.TournamentEntity.THUMBNAIL_DEFAULT_URL;
import static org.assertj.core.api.Assertions.assertThat;

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
@ServiceWithMySQLTest
class TournamentServiceTest {
    private TournamentService tournamentService;

    @Autowired
    private TournamentEntityRepository tournamentEntityRepository;

    @Autowired
    private GameEntityRepository gameEntityRepository;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(tournamentEntityRepository, gameEntityRepository);
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
        TournamentCategoryEnums category = TournamentCategoryEnums.GAME;

        try (MockedStatic<TournamentHelperService> tournamentHelperServiceMockedStatic = Mockito.mockStatic(TournamentHelperService.class)) {
            tournamentHelperServiceMockedStatic.when(TournamentHelperService::pickRandomUser).thenReturn(List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8"));

            // when
            tournamentService.startTournament(category);

            // then
            List<TournamentEntity> actual = tournamentEntityRepository.findTournamentEntityByCategory(category, PageRequest.of(0, 1));

            assertThat(actual).isNotEmpty();
            assertThat(actual.get(0).getCategory()).isEqualTo(category);
        }
    }

    @Test
    @DisplayName("16강 게임을 생성합니다. 16강 게임이 정상 생성되는지 확인한다.")
    void When_createSixteenthRoundGames_Then_createGames() {
        // given
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .category(TournamentCategoryEnums.GAME)
                .build();

        tournamentEntityRepository.save(tournamentEntity);

        List<String> users = List.of("user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8");

        // when
        tournamentService.createSixteenthRoundGames(tournamentEntity, users);

        // then
        List<GameEntity> founded = gameEntityRepository.findAll();

        assertThat(founded).isNotEmpty();
        assertThat(founded.size()).isEqualTo(4);

        assertThat(founded.get(0).getParticipantId1()).isEqualTo("user1");
        assertThat(founded.get(0).getParticipantId2()).isEqualTo("user2");

        assertThat(founded.get(1).getParticipantId1()).isEqualTo("user3");
        assertThat(founded.get(1).getParticipantId2()).isEqualTo("user4");

        assertThat(founded.get(2).getParticipantId1()).isEqualTo("user5");
        assertThat(founded.get(2).getParticipantId2()).isEqualTo("user6");

        assertThat(founded.get(3).getParticipantId1()).isEqualTo("user7");
        assertThat(founded.get(3).getParticipantId2()).isEqualTo("user8");
    }

    @Test
    void createSixteenthRoundGames() {
    }

    @Test
    void endLastTournament() {
    }

    @AfterEach
    void tearDown() {
    }
}