package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    default int findLikeCountById(Integer id) {
        return findById(id).map(UserInfo::getLikeCount).orElse(0);
    }

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT UI.user_id as `userId`, UP.image_key as `profileImageUrl` FROM USER_INFO AS 'UI' " +
            "LEFT JOIN USER_PROFILES AS 'UP' ON UI.user_id = UP.user_id" +
            " WHERE UP.image_key IS NULL OR UP.image_key != 'default_image'" +
            " ORDER BY RAND() LIMIT 16", nativeQuery = true)
    List<UserRandomResponseDto> findRandomUser();
}
