package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TeenService {

    private final UserService userService;
    private final LikeService likeService;

    public List<UserProfileResponseDto> getFamousTeenByGuest() {
        List<UserInfo> users = userService.findAll();

        return users.stream()
                .sorted(Comparator.comparingInt(user -> -user.getLikeCount())) // 좋아요 수에 따라 내림차순 정렬
                .limit(5) // 상위 5개 사용자만 선택
                .map(user -> toUserProfileResponseDto(user, Collections.emptySet()))
                .collect(Collectors.toList()); // 리스트로 수집

    }

    public List<UserProfileResponseDto> getFamousTeenByUser() {
        List<UserInfo> users = userService.findAll();

        return users.stream()
                .sorted(Comparator.comparingInt(user -> -user.getLikeCount())) // 좋아요 수에 따라 내림차순 정렬
                .limit(5) // 상위 5개 사용자만 선택
                .map(user -> toUserProfileResponseDto(user, likeService.getLikedUserIds(user.getUniqueId())))
                .collect(Collectors.toList()); // 리스트로 수집

    }

    private UserProfileResponseDto toUserProfileResponseDto(UserInfo user, Set<String> likedUserIds) {
        boolean isLiked =
                likedUserIds != null && likedUserIds.contains(String.valueOf(user.getId()));

        return new UserProfileResponseDto(user, isLiked);
    }


}
