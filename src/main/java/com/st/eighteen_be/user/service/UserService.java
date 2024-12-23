package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserRoles;
import com.st.eighteen_be.user.enums.CategoryType;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

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

    public Set<String> getRoles(UserInfo userInfo){
        Set<UserRoles> userRoles = userInfo.getRoles();
        Set<String> roles = new HashSet<>();

        for(UserRoles userRole : userRoles){
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

    public List<UserInfo> findAll(){
        return userRepository.findAll();
    }

    public Page<UserInfo> findAllByCategory(CategoryType category, Pageable pageable){
        return userRepository.findAllByCategory(category,pageable);
    }

    public Page<UserInfo> findPageBy(Pageable pageable){
        return userRepository.findPageBy(pageable);
    }

    int findLikeCountById(Integer id) {
        return userRepository.findById(id)
                .map(UserInfo::getLikeCount).orElse(0);
    }
    
    /**
     * 최근 2분간 수정된 사용자 정보를 Redis에 업데이트한다.
     */
    public void updateUserInfoToRedis() {
        return userRepository.findAllByLastModifiedDateAfter(LocalDateTime.now().minusMinutes(2))
    }
}
