package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserRandomResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Slice<UserInfo> findPageBy(Pageable pageable);

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT new com.st.eighteen_be.user.dto.response.UserRandomResponseDto(UI.uniqueId, UMD.imageKey) \
            FROM UserInfo AS UI \
            LEFT JOIN UserMediaData AS UMD ON UI.id = UMD.user.id \
             WHERE UI.tournamentJoin =  true \
             AND UI.category = :category \
             ORDER BY RAND() LIMIT 32""")
    List<UserRandomResponseDto> findRandomUser(@Param("category") CategoryType category);

    @Query("SELECT u FROM UserInfo u where u.category=:category")
    Slice<UserInfo> findAllByCategory(@Param("category") CategoryType category, Pageable pageable);
}
