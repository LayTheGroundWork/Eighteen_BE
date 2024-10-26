package com.st.eighteen_be.tournament_participant.service;

import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament_participant.service
 * fileName       : TournamentParticipantService
 * author         : Jun
 * date           : 24. 10. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 26.        Jun       최초 생성
 */
@Service
@RequiredArgsConstructor
public class TournamentParticipantService {
    private final TournamentEntityRepository tournamentEntityRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    
    public List<ThisWeekTournamentParticipantResponseDTO> showParticipantNowTournament() {
        // 최근 토너먼트를 카테고리별로 조회합니다.
        tournamentEntityRepository.findTopByOrderByTournamentNoDesc()
                                  .ifPresent(tournamentEntity -> {
                                      // 최근 토너먼트의 참가자를 조회합니다.
                                      List<TournamentParticipantEntity> tournamentParticipantEntities = tournamentParticipantRepository.findByTournament(tournamentEntity);
                                      // 최근 토너먼트의 참가자를 ThisWeekTournamentParticipantResponseDTO로 변환합니다.
                                      return tournamentParticipantEntities.stream()
                                                                         .map(TournamentParticipantEntity::toThisWeekTournamentParticipantResponseDTO)
                                                                         .toList();
                                  });
        
        return tournamentParticipantRepository.findAllByTournamentTournamentNo(tournamentNo)
                       .stream()
                       .map(TournamentParticipantEntity::toThisWeekTournamentParticipantResponseDTO)
                       .toList();
    }
}
