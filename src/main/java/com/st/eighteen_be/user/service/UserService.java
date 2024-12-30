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
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserSearchInfoRedisRepository userSearchInfoRedisRepository;
    
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

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

    public List<UserInfo> findAllByCategory(CategoryType category) {
        return userRepository.findAllByCategory(category);
    }

    public Page<UserInfo> findPageByCategory(CategoryType category, Pageable pageable) {
        return userRepository.findPageByCategory(category, pageable);
    }
    
    public Page<UserInfo> findPageBy(Pageable pageable) {
        return userRepository.findPageBy(pageable);
    }
    
    int findLikeCountById(Integer id) {
        return userRepository.findById(id)
                .map(UserInfo::getLikeCount).orElse(0);
    }

    public List<UserInfo> findCreatedUsers(LocalDateTime searchDateTime) {
        return userRepository.findAllByCreatedDateAfterOrLastModifiedDateAfter(searchDateTime, searchDateTime);
    }
    
    /**
     * 레디스에 해당 회원 저장 혹은 업데이트 수행
     *
     * @param users : 최근 5분간 새로 생긴 회원들
     */
    @Transactional(readOnly = true)
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
        
        // 패턴 생성
        String nickNamePattern = MessageFormat.format("userSearchInfo:nickName:{0}*", searchKey);
        String uniqueIdPattern = MessageFormat.format("userSearchInfo:{0}*", searchKey);
        
        // 닉네임으로 검색
        final Flux<String> nickNameValues = reactiveStringRedisTemplate.scan(
                ScanOptions.scanOptions().match(nickNamePattern).build()
        ).flatMap(key -> reactiveStringRedisTemplate.opsForSet().members(key))
                .map(value -> MessageFormat.format("userSearchInfo:{0}", value));
        
        // 아이디로 검색
        final Flux<String> idKey = reactiveStringRedisTemplate.scan(
                ScanOptions.scanOptions().match(uniqueIdPattern).build()
        ).filter(key -> !key.endsWith(":idx"));
        
        //닉네임과 아이디간의 중복 키 제거 -- 10개로 조회 제한 -- 내림차순
        final Flux<String> keyFlux = Flux.concat(nickNameValues, idKey).sort().distinct().take(10);
        
        // DTO로 매핑
        return keyFlux
                .flatMap(key ->
                        reactiveStringRedisTemplate.opsForHash()
                                .entries(key) // HashMap 형태로 데이터를 가져옴
                                .collectMap(Map.Entry::getKey, Map.Entry::getValue) // 필터링된 데이터를 Map으로 변환
                )
                .map(UserSearchInfoResponseDto::from);
    }
}
