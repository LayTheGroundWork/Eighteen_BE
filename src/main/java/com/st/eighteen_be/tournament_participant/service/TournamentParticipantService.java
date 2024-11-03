package com.st.eighteen_be.tournament_participant.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.BadRequestException;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
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
    private final VoteEntityRepository voteEntityRepository;
    private final TournamentEntityRepository tournamentEntityRepository;
    
    public List<ThisWeekTournamentParticipantResponseDTO> showParticipantNowTournament(CategoryType categoryType, String uniqueId) {
        //이번주 카테고리의 토너먼트에 참여했는지 검증해야한다.
        //해당 카테고리의 최신토너먼트 조회
        TournamentEntity lastestTournamentByCategory = tournamentEntityRepository.findFirstByCategoryOrderByCreatedDateDesc(categoryType)
                .orElseThrow(() -> new BadRequestException(ErrorCode.NOT_FOUND_TOURNAMENT));
        
        boolean hasAlreadyParticipated = voteEntityRepository.existsByTournamentAndVoterId(lastestTournamentByCategory, uniqueId);
        if (hasAlreadyParticipated) {
            throw new BadRequestException(ErrorCode.ALREADY_PARTICIPATED);
        }
        
        // 최근 토너먼트를 카테고리별로 조회합니다.
        return tournamentParticipantRepository.showParticipantForThisWeek(categoryType);
    }
}
