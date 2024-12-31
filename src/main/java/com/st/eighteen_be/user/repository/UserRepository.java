package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    Page<UserInfo> findPageBy(Pageable pageable);

    Optional<UserInfo> findByUniqueId(String uniqueId);

    Optional<UserInfo> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
            SELECT new com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto(UI.uniqueId, UMD.imageKey) \
            FROM UserInfo AS UI \
            LEFT JOIN UserMediaData AS UMD ON UI.id = UMD.user.id \
             WHERE UI.tournamentJoin =  true \
             AND UI.category = :category \
             AND UMD.imageKey IS NOT NULL \
             ORDER BY UI.likeCount desc LIMIT :limit""")
    List<MostLikedUserResponseDto> findRandomUsers(@Param("category") CategoryType category, int limit);

    @Query("SELECT u FROM UserInfo u where u.category=:category")
    List<UserInfo> findAllByCategory(@Param("category") CategoryType category);

    @Query("SELECT u FROM UserInfo u where u.category=:category")
    Page<UserInfo> findPageByCategory(@Param("category") CategoryType category, Pageable pageable);
    
    @Query("""
            SELECT new com.st.eighteen_be.user.dto.response.MostLikedUserResponseDto(UI.uniqueId, UMD.imageKey) \
            FROM UserLike AS UL \
            JOIN UL.user AS UI \
            LEFT JOIN UserMediaData AS UMD ON UI.id = UMD.user.id \
            WHERE UL.createdDate BETWEEN :start AND :end \
            AND UI.category = :category \
            AND UI.tournamentJoin = true \
            AND UMD.imageKey IS NOT NULL \
            GROUP BY UI.id, UMD.imageKey \
            ORDER BY COUNT(UL.id) DESC \
            LIMIT 16""")
    List<MostLikedUserResponseDto> findUsersByCategoryOrderByLastweekLikeCountLimit16(@Param("category") CategoryType category, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    List<UserInfo> findAllByCreatedDateAfterOrLastModifiedDateAfter(@Param("createdDate") LocalDateTime createdDate, @Param("lastModifiedDate") LocalDateTime lastModifiedDate);
}
