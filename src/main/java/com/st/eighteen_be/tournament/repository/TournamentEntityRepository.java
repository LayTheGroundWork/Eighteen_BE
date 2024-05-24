package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.TournamentEntity;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import com.st.eighteen_be.tournament.repository.querydsl.TournamentRepsitoryCustom;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentEntityRepository extends JpaRepository<TournamentEntity, Long>, TournamentRepsitoryCustom {
    Optional<TournamentEntity> findFirstByCategoryAndStatusIsTrueOrderByCreatedDateDesc(@Nonnull TournamentCategoryEnums category);
}