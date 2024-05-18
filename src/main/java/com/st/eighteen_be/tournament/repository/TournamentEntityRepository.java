package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TournamentEntityRepository extends JpaRepository<TournamentEntity, Long> {

    List<TournamentEntity> findTournamentEntityByCategory(@Nonnull String category, Pageable pageable);
}