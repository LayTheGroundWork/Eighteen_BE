package com.st.eighteen_be.tournament_participant.service;

import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.user.enums.CategoryType;
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
    private final TournamentParticipantRepository tournamentParticipantRepository;
    
    public List<ThisWeekTournamentParticipantResponseDTO> showParticipantEachCategory(CategoryType category) {
        return tournamentParticipantRepository.findAllByCategory(category);
    }
}
