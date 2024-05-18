package com.st.eighteen_be.tournament.service;

import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.repository.GameEntityRepository;
import com.st.eighteen_be.tournament.repository.TournamentEntityRepository;
import lombok.RequiredArgsConstructor;
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
public class TournamentService {
    private TournamentEntityRepository tournamentEntityRepository;
    private GameEntityRepository gameEntityRepository;
    
    public List<TournamentSearchResponseDTO> search(PageRequest pageRequest, String category) {
        
        return tournamentEntityRepository.findTournamentEntityByCategory(category, pageRequest).stream()
                .map(TournamentEntity::toTournamentSearchResponseDTO)
                .toList();
    }
    
    public void startTournament() {
        //토너먼트를 생성하고
        TournamentEntity created = TournamentEntity.builder().build();
        
        TournamentEntity savedTournament = tournamentEntityRepository.save(created);
        
        //토너먼트 참가자를 선정한다. - 선정된 참가자 16명이 될 예정.
        
        // 게임을 생성한다. - 게임은 16강만 만들어야함. ( 8 이후에 대해서는 사용자의 선택에 따라 나뉜다 )
    }
    
    public void endLastTournament() {
        //마지막 토너먼트를 종료시킨다.
        
        //승자를 선정한다.
    }
}