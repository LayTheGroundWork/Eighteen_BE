package com.st.eighteen_be.user.redishash;

import com.st.eighteen_be.user.dto.response.UserSearchInfoResponseDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Map;

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
    @Id
    private String uniqueId;
    
    private String thumbnailUrl;
    
    @Indexed
    private String nickName;
    
    @Builder
    public UserSearchInfoHash(String uniqueId, String thumbnailUrl, String nickName) {
        this.uniqueId = uniqueId;
        this.thumbnailUrl = thumbnailUrl;
        this.nickName = nickName;
    }
    
    public boolean matchesSearchKey(String searchKey) {
        return (uniqueId != null && uniqueId.startsWith(searchKey)) ||
                       (nickName != null && nickName.startsWith(searchKey));
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
    
    public static UserSearchInfoHash fromMap(Map<Object, Object> objectObjectMap) {
        return UserSearchInfoHash.builder()
                       .uniqueId((String) objectObjectMap.get("uniqueId"))
                       .thumbnailUrl((String) objectObjectMap.get("thumbnailUrl"))
                       .nickName((String) objectObjectMap.get("nickName"))
                       .build();
    }
}
