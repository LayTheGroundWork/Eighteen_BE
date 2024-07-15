package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    @Query("select u.likeCount from UserInfo u where u.id = ?")
    int getLikeCount(Integer Id);

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
}
