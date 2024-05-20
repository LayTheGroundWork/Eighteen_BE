package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentParticipantEntityRepository extends JpaRepository<TournamentParticipantEntity, Long> {
}