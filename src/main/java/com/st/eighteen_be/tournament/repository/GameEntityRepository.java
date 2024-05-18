package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {
}