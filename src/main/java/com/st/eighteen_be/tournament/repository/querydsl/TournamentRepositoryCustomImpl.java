package com.st.eighteen_be.tournament.repository.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.user.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.st.eighteen_be.tournament.domain.entity.QTournamentEntity.tournamentEntity;

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

    @Override
    public List<TournamentSearchResponseDTO> findTournamentByCategoryAndPaging(CategoryType category, Pageable pageable) {
        return qf.select(
                        Projections.constructor(
                                TournamentSearchResponseDTO.class,
                                tournamentEntity.tournamentNo,
                                tournamentEntity.thumbnailUrl,
                                tournamentEntity.status,
                                tournamentEntity.startDate,
                                tournamentEntity.endDate
                                )
                )
                .from(tournamentEntity)
                .where(eqCategory(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private static BooleanExpression eqCategory(CategoryType category) {
        if (category == null) {
            return null;
        }

        return tournamentEntity.category.eq(category);
    }
}
