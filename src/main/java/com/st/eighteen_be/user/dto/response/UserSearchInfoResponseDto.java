package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO for {@link UserSearchInfoHash}
 */
@Getter
@Builder
public class UserSearchInfoResponseDto {
    String uniqueId;
    String thumbnailUrl;
    String nickName;
    
    public static UserSearchInfoResponseDto from(UserSearchInfoHash userSearchInfoHash) {
        return UserSearchInfoResponseDto.builder()
                .uniqueId(userSearchInfoHash.getUniqueId())
                .thumbnailUrl(userSearchInfoHash.getThumbnailUrl())
                .nickName(userSearchInfoHash.getNickName())
                .build();
    }
}
