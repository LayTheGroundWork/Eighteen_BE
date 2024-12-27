package com.st.eighteen_be.user.dto.response;

import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * DTO for {@link UserSearchInfoHash}
 */
@Getter
@Builder
public class UserSearchInfoResponseDto {
    String uniqueId;
    String thumbnailUrl;
    String nickName;
    
    public static @NotNull UserSearchInfoResponseDto from(@NotNull Map<Object, Object> userSearchInfoHash) {
        return UserSearchInfoResponseDto.builder()
                .uniqueId(userSearchInfoHash.get("uniqueId").toString())
                .thumbnailUrl(userSearchInfoHash.get("thumbnailUrl").toString())
                .nickName(userSearchInfoHash.get("nickName").toString())
                .build();
    }
}
