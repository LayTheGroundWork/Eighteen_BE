package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.dto.response.UserSearchInfoResponseDto;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.redishash.UserSearchInfoHash;
import com.st.eighteen_be.user.repository.UserRepository;
import com.st.eighteen_be.user.repository.UserSearchInfoRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserSearchInfoRedisRepository userSearchInfoRedisRepository;
    private final ReactiveRedisTemplate<String, UserSearchInfoHash> reactiveRedisTemplate;
    
    @Transactional
    public void save(UserInfo userInfo) {
        userRepository.save(userInfo);
    }
    
    @Transactional
    public String delete(String uniqueId) {
        UserInfo userInfo = findByUniqueId(uniqueId);
        userRepository.delete(userInfo);
        
        return uniqueId;
    }
    
    public Set<String> getRoles(UserInfo userInfo) {
        Set<UserRoles> userRoles = userInfo.getRoles();
        Set<String> roles = new HashSet<>();
        
        for (UserRoles userRole : userRoles) {
            roles.add(userRole.getRole().getValue());
        }
        
        return roles;
    }
    
    public UserInfo findById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }
    
    public UserInfo findByUniqueId(String uniqueId) {
        return userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }
    
    public UserInfo findByPhoneNumber(String phone) {
        return userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }
    
    public List<UserInfo> findAll() {
        return userRepository.findAll();
    }
    
    public Page<UserInfo> findAllByCategory(CategoryType category, Pageable pageable) {
        return userRepository.findAllByCategory(category, pageable);
    }
    
    public Page<UserInfo> findPageBy(Pageable pageable) {
        return userRepository.findPageBy(pageable);
    }
    
    int findLikeCountById(Integer id) {
        return userRepository.findById(id)
                .map(UserInfo::getLikeCount).orElse(0);
    }
    
    
    /**
     * 최근 5분간 새로 생긴 회원을 redis 에 insert 하고 lastModifiedDate 가 5분 내인 회원에 대해 업데이트를 수행한다.
     */
    @Transactional(readOnly = false)
    public void updateUserInfoToRedis() {
        // 5분 전 시간
        final LocalDateTime searchDateTime = LocalDateTime.now().minusMinutes(5);
        
        // 5분 전 시간 이후에 생성된 회원들을 조회
        List<UserInfo> createdUsers = userRepository.findAllByCreatedDateAfterOrLastModifiedDateAfter(searchDateTime, searchDateTime);
        this.saveMainUserInfoInRedis(createdUsers);
    }
    
    /**
     * 레디스에 해당 회원 저장 혹은 업데이트 수행
     *
     * @param users : 최근 5분간 새로 생긴 회원들
     */
    @Transactional(readOnly = false)
    public void saveMainUserInfoInRedis(List<UserInfo> users) {
        final List<UserSearchInfoHash> beSavedUser = users.stream()
                .map(UserInfo::toUserSearchInfoHash)
                .toList();
        
        userSearchInfoRedisRepository.saveAll(beSavedUser);
    }
    
    public Flux<UserSearchInfoResponseDto> findAllUserSearchInfo(String searchKey) {
        if (Objects.isNull(searchKey) || searchKey.isBlank()) {
            return Flux.empty();
        }
        
        //searchkey는  uniqueId 혹은 nickName reactiveRedisTemplate
        return reactiveRedisTemplate.opsForHash()
                .values("userSearchInfo").log()
                .cast(UserSearchInfoHash.class)
                .log()
                .filter(user -> user.getUniqueId().startsWith(searchKey) || user.getNickName().startsWith(searchKey))
                .map(UserSearchInfoHash::toResponseDto);
    }
}
