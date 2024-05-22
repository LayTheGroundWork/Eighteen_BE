package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.annotation.ServiceWithMySQLTest;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantEntityRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.tournament.service.helper.TournamentHelperService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.TournamentEntity.THUMBNAIL_DEFAULT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
class TournamentServiceTest {
    private TournamentService tournamentService;
    
    @Autowired
    private TournamentEntityRepository tournamentEntityRepository;
    
    @Autowired
    private TournamentParticipantEntityRepository tournamentParticipantEntityRepository;
    
    @Autowired
    private VoteEntityRepository voteEntityRepository;
    
    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(tournamentEntityRepository, tournamentParticipantEntityRepository, voteEntityRepository);
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
        
        try (MockedStatic<TournamentHelperService> tournamentHelperServiceMockedStatic = Mockito.mockStatic(TournamentHelperService.class)) {
            tournamentHelperServiceMockedStatic.when(TournamentHelperService::pickRandomUser).thenReturn(tournamentParticipantEntities);
            
            // when
            tournamentService.startTournament();
            
            // then
            List<TournamentEntity> actual = tournamentEntityRepository.findAll();
            
            assertThat(actual).isNotEmpty().hasSize(TournamentCategoryEnums.values().length);
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
                    .allMatch(tournamentEntity -> tournamentEntity.isStatus() == false, "토너먼트 상태값이 변경되지 않았습니다.");
        }
        
        @Test
        @DisplayName("토너먼트 종료시 토너먼트 투표순으로 승자를 선정한다.")
        void When_endTournament_Then_determineWinner() {
            // given
            TournamentEntity tournamentEntity = TournamentEntity.builder()
                    .category(TournamentCategoryEnums.GAME)
                    .build();
            
            tournamentEntityRepository.save(tournamentEntity);
            
            //참여자 추가하기
            List<TournamentParticipantEntity> tournamentParticipantEntities = List.of(
                    TournamentParticipantEntity.of("user1"),
                    TournamentParticipantEntity.of("user2")
            );
            
            tournamentParticipantEntityRepository.saveAll((tournamentParticipantEntities));
            
            //투표자들 추가하기
            Result result = getResult(tournamentEntity, tournamentParticipantEntities);
            
            voteEntityRepository.saveAll(List.of(result.voter1(), result.voter2(), result.voter3()));
            
            // when
            List<TournamentVoteResultResponseDTO> actual = tournamentService.determineWinner(tournamentEntity.getTournamentNo());
            
            // then
            assertThat(actual).isNotEmpty();
            assertThat(actual.size()).isEqualTo(2);
            
            assertSoftly(
                    softly -> {
                        softly.assertThat(actual.get(0).getRankerId()).isEqualTo("user1");
                        softly.assertThat(actual.get(0).getRank()).isEqualTo(1);
                        softly.assertThat(actual.get(0).getVoteCount()).isEqualTo(2);
                        
                        softly.assertThat(actual.get(1).getRankerId()).isEqualTo("user2");
                        softly.assertThat(actual.get(1).getRank()).isEqualTo(2);
                        softly.assertThat(actual.get(1).getVoteCount()).isEqualTo(1);
                    }
            );
        }
        
        private static @NotNull Result getResult(TournamentEntity tournamentEntity, List<TournamentParticipantEntity> tournamentParticipantEntities) {
            VoteEntity voter1 = VoteEntity.builder()
                    .tournament(tournamentEntity)
                    .participant(tournamentParticipantEntities.get(0))
                    .voterId("voter1")
                    .build();
            
            VoteEntity voter2 = VoteEntity.builder()
                    .tournament(tournamentEntity)
                    .participant(tournamentParticipantEntities.get(0))
                    .voterId("voter2")
                    .build();
            
            VoteEntity voter3 = VoteEntity.builder()
                    .tournament(tournamentEntity)
                    .participant(tournamentParticipantEntities.get(1))
                    .voterId("voter3")
                    .build();
            
            return new Result(voter1, voter2, voter3);
        }
        
        private record Result(VoteEntity voter1, VoteEntity voter2, VoteEntity voter3) {}
    }
}