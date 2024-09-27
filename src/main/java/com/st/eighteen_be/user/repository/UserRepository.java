package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT new com.st.eighteen_be.user.dto.response.UserRandomResponseDto(UI.uniqueId, UP.imageKey) "+
            "FROM UserInfo AS UI " +
            "LEFT JOIN UserProfiles AS UP ON UI.id = UP.user.id" +
            " WHERE UP.imageKey IS NULL OR UP.imageKey != 'default_image'" +
            " ORDER BY RAND() LIMIT 32")
    List<UserRandomResponseDto> findRandomUser();

    @Query("SELECT u FROM UserInfo u where u.category=:category")
    List<UserInfo> findAllByCategory(@Param("category") CategoryType category);
}
