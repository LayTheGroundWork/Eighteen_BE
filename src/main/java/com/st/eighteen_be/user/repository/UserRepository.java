package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Slice<UserInfo> findPageBy(Pageable pageable);

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT new com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto(UI.uniqueId, UMD.imageKey) \
            FROM UserInfo AS UI \
            LEFT JOIN UserMediaData AS UMD ON UI.id = UMD.user.id \
             WHERE UI.tournamentJoin =  true \
             AND UI.category = :category \
             ORDER BY UI.likeCount desc LIMIT :limit""")
    List<MostLikedUserResponseDto> findRandomUsers(@Param("category") CategoryType category, int limit);

    @Query("SELECT u FROM UserInfo u where u.category=:category")
    Slice<UserInfo> findAllByCategory(@Param("category") CategoryType category, Pageable pageable);
    
    @Query("""
            SELECT new com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto(UI.uniqueId, UMD.imageKey) \
            FROM UserLike AS UL \
            JOIN UL.user AS UI \
            LEFT JOIN UserMediaData AS UMD ON UI.id = UMD.user.id \
            WHERE UL.createdDate BETWEEN :start AND :end \
            AND UI.category = :category \
            GROUP BY UI.id, UMD.imageKey \
            ORDER BY COUNT(UL.id) DESC""")
    List<MostLikedUserResponseDto> findUsersByCategoryOrderByLastweekLikeCount(@Param("category") CategoryType category, LocalDateTime start, LocalDateTime end);
}
