package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserSnsLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnsLinkRepository extends JpaRepository<UserSnsLink, Integer> {
}
