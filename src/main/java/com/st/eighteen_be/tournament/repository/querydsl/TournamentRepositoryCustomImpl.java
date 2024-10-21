package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.QTournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.user.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.QTournamentEntity.tournamentEntity;
import static com.st.eighteen_be.tournament.domain.entity.QTournamentParticipantEntity.tournamentParticipantEntity;

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
        //토너먼트
        qf.select(
                        new QTournamentSearchResponseDTO(
                                tournamentParticipantEntity.tournament,
                                
                        ))
                .from(tournamentParticipantEntity)
                .leftJoin(tournamentParticipantEntity.tournament)
                .orderBy(tournamentParticipantEntity.tournament.tournamentNo.desc())
                .fetch();
        
        return List.of();
    }
    
    private static BooleanExpression eqCategory(CategoryType category) {
        if (category == null) {
            return null;
        }
        
        return tournamentEntity.category.eq(category);
    }
}
