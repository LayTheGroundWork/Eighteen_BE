package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.repository.querydsl.TournamentRepositoryCustom;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentEntityRepository extends JpaRepository<TournamentEntity, Long>, TournamentRepositoryCustom {
    Optional<TournamentEntity> findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(@Nonnull CategoryType category);
    
    Optional<TournamentEntity> findFirstByCategoryOrderByCreatedDateDesc(@Nonnull CategoryType category);
}
