package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import com.st.eighteen_be.tournament.domain.redishash.ThisWeekTournamentParticipantResponseDTO;
import com.st.eighteen_be.tournament.repository.querydsl.TournamentParticipantRepositoryCustom;
import com.st.eighteen_be.user.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipantEntity, Long>, TournamentParticipantRepositoryCustom {

    List<TournamentParticipantEntity> findByTournament(TournamentEntity tournament);
    
    List<ThisWeekTournamentParticipantResponseDTO> findAllByCategory(CategoryType category);
}
