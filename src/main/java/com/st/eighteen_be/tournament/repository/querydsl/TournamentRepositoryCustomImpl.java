package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.entity.QTournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity;
import com.st.eighteen_be.tournament_winner.domain.QTournamentWinnerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.tournament.repository.querydsl
 * fileName       : TournamentRepsitoryCustomImpl
 * author         : ipeac
 * date           : 24. 5. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 24.        ipeac       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class TournamentRepositoryCustomImpl implements TournamentRepositoryCustom {
    
    private final JPAQueryFactory qf;
    
    /*토너먼트 검색이 {   "status": 200,   "data": [     {
             토너먼트 카테고리,
             토너먼트 우승자들 : [
                      {
                       토너먼트 회차
                       토너먼트 pk
                        우승자 사진
                 }
              ]     }   ],   "message": "string" } 
    한 줄은  일주일 단위이고 옆으로 스크롤은 역대 우승자
    형태로 만들어야함
    */
    @Override
    public List<TournamentSearchResponseDTO> findTournamentMainInfos() {
        QTournamentEntity t = QTournamentEntity.tournamentEntity;
        QTournamentWinnerEntity tw = QTournamentWinnerEntity.tournamentWinnerEntity;
        QTournamentParticipantEntity tp = QTournamentParticipantEntity.tournamentParticipantEntity;
        
        // 평평한 데이터 조회
        List<Tuple> tuples = qf
                .select(
                        t.category,
                        t.season,
                        t.tournamentNo,
                        tp.userImageUrl
                )
                .from(t)
                .leftJoin(tw).on(tw.winningTournamentNo.eq(t.tournamentNo))
                .leftJoin(tp).on(tw.participantNo.eq(tp.participantNo))
                .orderBy(t.category.asc(), t.tournamentNo.desc())
                .fetch();
        
        return List.of();
    }
}
