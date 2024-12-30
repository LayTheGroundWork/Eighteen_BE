package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserMediaData;
import com.st.eighteen_be.user.domain.UserQuestion;
import com.st.eighteen_be.user.dto.request.MyPageRequestDto;
import com.st.eighteen_be.user.dto.response.*;
import com.st.eighteen_be.user.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDtoService {

    private final UserService userService;
    private final LikeService likeService;
    private final S3Service s3Service;

    public String delete(String uniqueId) {
        return userService.delete(uniqueId);
    }

    public void addLike(String uniqueId, Integer likedId) {
        likeService.addLike(uniqueId, likedId);
    }

    public void cancelLike(String uniqueId, Integer likedId) {
        likeService.cancelLike(uniqueId, likedId);
    }

    public UserDetailsResponseDto findById(Integer userId) {
        UserInfo userInfo = userService.findById(userId);
        int likeCount = likeService.countLikes(userInfo.getId());

        return getUserDetailsResponseDto(userInfo, likeCount);
    }

    public UserDetailsResponseDto findByUniqueId(String uniqueId) {
        UserInfo userInfo = userService.findByUniqueId(uniqueId);
        int likeCount = likeService.countLikes(userInfo.getId());

        return getUserDetailsResponseDto(userInfo, likeCount);
    }

    private UserDetailsResponseDto getUserDetailsResponseDto(UserInfo userInfo, int likeCount) {
        List<String> images = getImages(userInfo);
        List<UserQuestion> questions = userInfo.getUserQuestions();

        List<UserQuestionResponseDto> responseDtoList = questions.stream()
                .map(UserQuestionResponseDto::new)
                .toList();

        return new UserDetailsResponseDto(userInfo, likeCount, images, responseDtoList);
    }

    @Transactional(readOnly = true)
    public UserProfilePageResponseDto getUserProfilePage(Pageable pageable) {
        Page<UserInfo> users = userService.findPageBy(pageable);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user, Collections.emptySet()))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return new UserProfilePageResponseDto(responseDtoList, users.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserProfilePageResponseDto getUserProfilesWithLikes(String uniqueId, Pageable pageable) {
        Page<UserInfo> users = userService.findPageBy(pageable);
        return getUserProfilePageResponseDto(uniqueId, users);
    }

    @NotNull
    private UserProfilePageResponseDto getUserProfilePageResponseDto(String uniqueId, Page<UserInfo> users) {
        Set<String> likedUserIds = likeService.getLikedUserIds(uniqueId);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user, likedUserIds))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return new UserProfilePageResponseDto(responseDtoList, users.getTotalPages());
    }

    @Transactional(readOnly = true)
    public UserProfilePageResponseDto getUserProfilesWithCategory(CategoryType category, Pageable pageable) {
        Page<UserInfo> users = userService.findAllByCategory(category, pageable);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user, Collections.emptySet()))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return new UserProfilePageResponseDto(responseDtoList, users.getTotalPages());

    }

    public UserProfilePageResponseDto getUserProfilesWithLikeStatusAndCategory(String uniqueId, CategoryType category, Pageable pageable) {
        Page<UserInfo> users = userService.findAllByCategory(category, pageable);
        return getUserProfilePageResponseDto(uniqueId, users);
    }

    private List<String> getImages(UserInfo userInfo) {
        return s3Service.getPreSignedURLsForFolder(userInfo.getUniqueId());
    }

    private UserProfileResponseDto toUserProfileResponseDto(UserInfo user, Set<String> likedUserIds) {
        boolean isLiked =
                likedUserIds != null && likedUserIds.contains(String.valueOf(user.getId()));

        return new UserProfileResponseDto(user, isLiked);
    }

    public void myPageUpdate(String uniqueId, MyPageRequestDto requestDto) {
        UserInfo userInfo = userService.findByUniqueId(uniqueId);
        userInfo.myPageUpdate(requestDto);
    }

    public void profileDelete(String uniqueId, String imageKey) {
        UserInfo userInfo = userService.findByUniqueId(uniqueId);
        List<UserMediaData> mediaDataList = userInfo.getMediaDataList();

        for (UserMediaData mediaData : mediaDataList) {
            if (mediaData.getImageKey().equals(imageKey)) {
                mediaDataList.remove(mediaData);
                s3Service.delete(imageKey, uniqueId);
            }
        }
    }

    /**
     * 최근 5분간 새로 생긴 회원을 redis 에 insert 하고 lastModifiedDate 가 5분 내인 회원에 대해 업데이트를 수행한다.
     */
    @Transactional(readOnly = false)
    public void updateUserInfoToRedis() {
        // 5분 전 시간
        final LocalDateTime searchDateTime = LocalDateTime.now().minusMinutes(5);

        // 5분 전 시간 이후에 생성된 회원들을 조회
        List<UserInfo> createdUsers = userService.findCreatedUsers(searchDateTime);
        userService.saveMainUserInfoInRedis(createdUsers);
    }

    public Flux<UserSearchInfoResponseDto> findAllUserSearchInfo(String keyword) {
        return userService.findAllUserSearchInfo(keyword);
    }

}
