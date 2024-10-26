package com.st.eighteen_be.tournament_participant.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament_participant.service.TournamentParticipantService;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.st.eighteen_be.tournament_participant.integration
 * fileName       : TournamentParticipantIntegerationTest
 * author         : Jun
 * date           : 24. 10. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 26.        Jun       최초 생성
 */
@DisplayName("토너먼트 참가자 API 통합 테스트")
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TournamentParticipantIntegerationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TournamentParticipantService tournamentParticipantService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EntityManager em;
    
    @Test
    @DisplayName("이번주 토너먼트 참가자 정상 조회")
    void When_SearchThisWeekTournamentParticipant_Then_ReturnThisWeekTournamentParticipant() {
        // given
        //토너먼트 생성 - 참가자 채워넣기
        TournamentEntity tournamentEntity = TournamentEntity.createTournamentEntity(CategoryType.ART, 1);
        em.persist(tournamentEntity);
        
        //토너먼트 참가자 넣기
        TournamentParticipantEntity tournamentParticipantEntity = TournamentParticipantEntity.builder()
                .tournament(tournamentEntity)
                .userId("tester1")
                .userImageUrl("http://test.com")
                .build();
        
        em.persist(tournamentParticipantEntity);
        
        // when
        List<ThisWeekTournamentParticipantResponseDTO> participants = tournamentParticipantService.showParticipantNowTournament();
        
        // then
        assertThat(participants.size()).isEqualTo(1);
        assertThat(participants.get(0).userId()).isEqualTo("tester1");
        assertThat(participants.get(0).profileImageUrl()).isEqualTo("http://test.com");
    }
}
