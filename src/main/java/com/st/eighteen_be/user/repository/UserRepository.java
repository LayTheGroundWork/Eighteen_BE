package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    default int findLikeCountById(Integer id) {
        return findById(id).map(UserInfo::getLikeCount).orElse(0);
    }

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
