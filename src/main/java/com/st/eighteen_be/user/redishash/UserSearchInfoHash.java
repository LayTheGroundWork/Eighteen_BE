package com.st.eighteen_be.user.redishash;

import com.st.eighteen_be.user.dto.response.UserSearchInfoResponseDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * packageName    : com.st.eighteen_be.user.redishash
 * fileName       : UserSearchInfoHash
 * author         : ipeac
 * date           : 24. 12. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 12. 23.        ipeac       최초 생성
 */
@RedisHash("userSearchInfo")
@Getter
public class UserSearchInfoHash implements Serializable {
    private static final String USER_SEARCH_INFO_PREFIX = "userSearchInfo:";
    
    @Id
    private final String id;
    
    @Indexed
    private final String uniqueId;
    
    private final String thumbnailUrl;
    
    @Indexed
    private final String nickName;
    
    @Builder
    public UserSearchInfoHash(String uniqueId, String thumbnailUrl, String nickName) {
        this.id = makeId(uniqueId);
        this.uniqueId = uniqueId;
        this.thumbnailUrl = thumbnailUrl;
        this.nickName = nickName;
    }
    
    @NotNull
    private static String makeId(String uniqueId) {
        return USER_SEARCH_INFO_PREFIX + uniqueId;
    }
    
    public static UserSearchInfoHash of(String uniqueId, String thumbnailUrl, String nickName) {
        return UserSearchInfoHash.builder()
                .uniqueId(uniqueId)
                .thumbnailUrl(thumbnailUrl)
                .nickName(nickName)
                .build();
    }
    
    public UserSearchInfoResponseDto toResponseDto() {
        return UserSearchInfoResponseDto.builder()
                .uniqueId(uniqueId)
                .thumbnailUrl(thumbnailUrl)
                .nickName(nickName)
                .build();
    }
}
