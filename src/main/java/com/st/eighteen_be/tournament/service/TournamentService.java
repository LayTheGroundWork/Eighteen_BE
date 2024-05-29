package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.tournament.api.TournamentVoteRequestDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentVoteResultResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentParticipantRepository;
import com.st.eighteen_be.tournament.repository.VoteEntityRepository;
import com.st.eighteen_be.tournament.service.helper.TournamentHelperService;
import lombok.NonNull;
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
    private final TournamentParticipantRepository tournamentParticipantEntityRepository;
    private final VoteEntityRepository voteEntityRepository;
    
    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, TournamentCategoryEnums category) {
        log.info("search start category : {}", category);
        
        return tournamentEntityRepository.findTournamentByCategoryAndPaging(category, pageRequest);
    }
    
    @Transactional(readOnly = false)
    public void startTournament() {
        log.info("startTournament start");
        
        //카테고리 별로 최신 시즌 조회 후 없으면 토너먼트를 생성한다.
        findLastestTournamentsGroupByCategory();
    }
    
    private void findLastestTournamentsGroupByCategory() {
        log.info("findLastestTournamentsGroupByCategory start");
        
        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            TournamentEntity lastestTournament = tournamentEntityRepository.findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(category)
                    .orElse(null);
            
            int newSeason = lastestTournament == null ? 1 : lastestTournament.getSeason() + 1;
            createNewTournament(category, newSeason);
            pickRandomUser();
        }
    }
    
    private void pickRandomUser() {
        log.info("pickRandomUser start");
        
        //TODO : 토너먼트 참가자를 (미정)으로 선정한다.
        tournamentParticipantEntityRepository.saveAll(TournamentHelperService.pickRandomUser());
    }
    
    @Transactional(readOnly = false)
    public TournamentEntity createNewTournament(TournamentCategoryEnums category, int season) {
        log.info("createNewTournament start category : {}", category);
        
        TournamentEntity created = TournamentEntity.createTournamentEntity(category, season);
        
        return tournamentEntityRepository.save(created);
    }
    
    @Transactional(readOnly = false)
    public void endLastestTournaments() {
        log.info("endLastestTournaments start");
        
        for (TournamentCategoryEnums category : TournamentCategoryEnums.values()) {
            TournamentEntity foundTournamet = endTournamentByCategory(category);
            determineWinner(foundTournamet.getTournamentNo());
        }
    }
    
    private TournamentEntity endTournamentByCategory(TournamentCategoryEnums category) {
        log.info("endTournamentByCategory start category : {}", category.getCategory());
        
        return tournamentEntityRepository.findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(category)
                .map(tournament -> {
                    tournament.endTournament();
                    return tournamentEntityRepository.save(tournament);
                })
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }
    
    @Transactional(readOnly = false)
    public List<TournamentVoteResultResponseDTO> determineWinner(@NonNull Long tournamentNo) {
        log.info("determineWinner start");
        
        List<TournamentVoteResultResponseDTO> voteResult = voteEntityRepository.findTournamentVoteResult(tournamentNo);
        
        setRank(voteResult);
        
        return voteResult;
    }
    
    private void setRank(List<TournamentVoteResultResponseDTO> voteResult) {
        long rank = 1;
        
        for (TournamentVoteResultResponseDTO tournamentVoteResultResponseDTO : voteResult) {
            tournamentVoteResultResponseDTO.setRank(rank++);
        }
    }
    
    @Transactional(readOnly = false)
    public void processVote(List<TournamentVoteRequestDTO> voteRequests) {
        log.info("processVote start");
        
        tournamentParticipantEntityRepository.updateVotePoints(voteRequests);
        tournamentParticipantEntityRepository.insertVoteRecord(voteRequests);
    }
}