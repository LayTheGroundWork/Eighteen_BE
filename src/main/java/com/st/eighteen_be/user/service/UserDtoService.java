package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserQuestion;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.dto.response.UserQuestionResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserDtoService {

    private final UserService userService;
    private final LikeService likeService;
    private final S3Service s3Service;

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

    public UserProfileResponseDto findUserProfileByUniqueId(String uniqueId, String accessToken) {
        UserInfo userInfo = userService.findByUniqueId(uniqueId);

        return new UserProfileResponseDto(userInfo, likeService.getLikedUserId(accessToken,userInfo.getId()));
    }

    public List<UserProfileResponseDto> getUserProfilePage(Pageable pageable){
        Slice<UserInfo> users = userService.findPageBy(pageable);
        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user,Collections.emptySet()))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return responseDtoList;
    }

    public List<UserProfileResponseDto> getUserProfilesWithLikes(String accessToken, Pageable pageable) {
        Slice<UserInfo> users = userService.findPageBy(pageable);
        Set<String> likedUserIds = likeService.getLikedUserIds(accessToken);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user,likedUserIds))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return responseDtoList;
    }

    public List<UserProfileResponseDto> getUserProfilesWithCategory(String category, Pageable pageable){
        Slice<UserInfo> users = userService.findAllByCategory(
                CategoryType.of(category),pageable);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user,Collections.emptySet()))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return responseDtoList;

    }

    public List<UserProfileResponseDto> getUserProfilesWithLikeStatusAndCategory(String accessToken, String category,
                                                                                 Pageable pageable){
        Slice<UserInfo> users = userService.findAllByCategory(CategoryType.of(category),pageable);
        Set<String> likedUserIds = likeService.getLikedUserIds(accessToken);

        List<UserProfileResponseDto> responseDtoList = users.stream()
                .map(user -> toUserProfileResponseDto(user,likedUserIds))
                .collect(Collectors.toList());

        Collections.shuffle(responseDtoList);

        return responseDtoList;
    }

    private UserDetailsResponseDto getUserDetailsResponseDto(UserInfo userInfo, int likeCount) {
        List<String> images = getImages(userInfo);
        List<UserQuestion> questions = userInfo.getUserQuestions();

        List<UserQuestionResponseDto> responseDtoList = questions.stream()
                .map(UserQuestionResponseDto::new)
                .toList();

        return new UserDetailsResponseDto(userInfo,likeCount,images,responseDtoList);
    }

    private List<String> getImages(UserInfo userInfo) {
        return s3Service.getPreSignedURLsForFolder(userInfo.getUniqueId());
    }

    private UserProfileResponseDto toUserProfileResponseDto(UserInfo user, Set<String> likedUserIds) {
        boolean isLiked =
                likedUserIds != null && likedUserIds.contains(String.valueOf(user.getId()));

        return new UserProfileResponseDto(user, isLiked);
    }
}
