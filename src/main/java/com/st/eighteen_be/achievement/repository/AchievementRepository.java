package com.st.eighteen_be.achievement.repository;

import com.st.eighteen_be.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement,Integer> {
}
