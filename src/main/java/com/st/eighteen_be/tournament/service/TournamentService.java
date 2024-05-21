package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantEntityRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.tournament.service.helper.TournamentHelperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.api
 * fileName       : TournamentService
 * author         : ipeac
 * date           : 24. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 15.        ipeac       최초 생성
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TournamentService {
    private final TournamentEntityRepository tournamentEntityRepository;
    private final TournamentParticipantEntityRepository tournamentParticipantEntityRepository;
    private final VoteEntityRepository voteEntityRepository;
    
    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, TournamentCategoryEnums category) {
        log.info("search start category : {}", category);
        
        return tournamentEntityRepository.findTournamentEntityByCategory(category, pageRequest).stream()
                .map(TournamentEntity::toTournamentSearchResponseDTO)
                .toList();
    }
    
    @Transactional(readOnly = false)
    public void startTournament() {
        log.info("startTournament start");
        
        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            startTournamentByCategory(category);
        }
    }
    
    private void startTournamentByCategory(TournamentCategoryEnums category) {
        createNewTournament(category);
        
        //TODO : 토너먼트 참가자를 랜덤으로 선정한다.
        tournamentParticipantEntityRepository.saveAll(TournamentHelperService.pickRandomUser());
    }
    
    @Transactional(readOnly = false)
    public TournamentEntity createNewTournament(TournamentCategoryEnums category) {
        log.info("createNewTournament start category : {}", category);
        
        TournamentEntity created = TournamentEntity.createTournamentEntity(category);
        
        return tournamentEntityRepository.save(created);
    }
    
    @Transactional(readOnly = false)
    public void endLastestTournaments() {
        log.info("endLastestTournaments start");
        
        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            endTournamentByCategory(category);
        }
        
        //TODO 승자를 선정한다.
    }
    
    private void endTournamentByCategory(TournamentCategoryEnums category) {
        log.info("endTournamentByCategory start category : {}", category.getCategory());
        
        tournamentEntityRepository.findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(category).ifPresent(
                tournament -> {
                    tournament.endTournament();
                    tournamentEntityRepository.save(tournament);
                    log.info("tournament end success tournamentNo : {}", tournament.getTournamentNo());
                }
        );
    }
    
    @Transactional(readOnly = false)
    public List<TournamentVoteResultResponseDTO> determineWinner(TournamentEntity tournament) {
        log.info("determineWinner start and tournament`s id : {}", tournament.getTournamentNo());
        
        return voteEntityRepository.findTournamentVoteResult(tournament.getTournamentNo());
    }
    
    
    //토너먼트에 대한 투표자 정보를 가져온다.
    private void getVoterInfo() {
        //TODO 투표자 정보를 가져온다.
    }
}