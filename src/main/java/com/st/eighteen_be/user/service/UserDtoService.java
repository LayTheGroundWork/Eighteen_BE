package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserQuestion;
import com.st.eighteen_be.user.dto.response.UserDetailsResponseDto;
import com.st.eighteen_be.user.dto.response.UserProfileResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<UserProfileResponseDto> getUserProfilesWithLikes(String accessToken) {
        List<UserInfo> users = userService.findAll();
        Set<String> likedUserIds = likeService.getLikedUserIds(accessToken);

        return users.stream()
                .map(user -> toUserProfileResponseDto(user, likedUserIds))
                .collect(Collectors.toList());
    }

    public List<UserProfileResponseDto> getUserProfilesWithCategory(String accessToken, String category){
        List<UserInfo> users = userService.findAllByCategory(CategoryType.of(category));
        Set<String> likedUserIds = likeService.getLikedUserIds(accessToken);

        return users.stream()
                .map(user -> toUserProfileResponseDto(user, likedUserIds))
                .collect(Collectors.toList());

    }

    private UserDetailsResponseDto getUserDetailsResponseDto(UserInfo userInfo, int likeCount) {
        List<String> images = getImages(userInfo);
        Map<String,String> qna = new HashMap<>();

        for(UserQuestion question : userInfo.getUserQuestions()){
            qna.put(question.getQuestion().getQuestion(),question.getAnswer());
        }

        return new UserDetailsResponseDto(userInfo,likeCount,images,qna);
    }

    private List<String> getImages(UserInfo userInfo) {
        return s3Service.getPreSignedURLsForFolder(userInfo.getUniqueId());
    }

    private UserProfileResponseDto toUserProfileResponseDto(UserInfo user, Set<String> likedUserIds) {
        boolean isLiked = likedUserIds != null && likedUserIds.contains(user.getId().toString());
        return new UserProfileResponseDto(user, isLiked);
    }
}
