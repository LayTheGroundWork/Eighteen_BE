package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.tournament.domain.redishash.RandomUser;
import lombok.*;

/**
 * packageName    : com.st.eighteen_be.user.dto.response
 * fileName       : UserRandomResponseDto
 * author         : ipeac
 * date           : 24. 8. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 28.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserRandomResponseDto {
    private Integer userId;
    private String profileImageUrl;

    public static UserRandomResponseDto of(Integer userId, String profileImageUrl) {
        return UserRandomResponseDto.builder()
                .userId(userId)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public RandomUser toRandomUser() {
        return RandomUser.builder()
                .userId(String.valueOf(userId))
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
