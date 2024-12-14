package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import com.st.eighteen_be.tournament.repository.querydsl.VoteRepositoryCustom;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteEntityRepository extends JpaRepository<VoteEntity, Long>, VoteRepositoryCustom {
    boolean existsByTournamentCategoryAndVoterId(@NotNull CategoryType tournament_category, @Size(max = 50) String voterId);
    
    boolean existsByTournamentAndVoterId(@NotNull TournamentEntity tournament, @Size(max = 50) String voterId);
    
    boolean existsByTournament_TournamentNoAndVoterId(@NotNull Long tournamentNo, @Size(max = 50) String voterId);
}
