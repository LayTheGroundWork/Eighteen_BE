package com.st.eighteen_be.user.redishash;

import jakarta.persistence.Id;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisHash;

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
public class UserSearchInfoHash {
    private static final String USER_SEARCH_INFO_PREFIX = "userSearchInfo:";
    
    @Id
    private String id;
    private String uniqueId;
    private String thumbnailUrl;
    private String nickName;
    
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
}
