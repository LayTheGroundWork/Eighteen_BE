package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserSnsLink;
import com.st.eighteen_be.user.repository.SnsLinkRepository;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnsLinkService {

    private final UserRepository userRepository;
    private final SnsLinkRepository snsLinkRepository;

    @Transactional
    public void addSnsLink(Integer userId, List<String> snsLinks) {
        UserInfo userInfo = userRepository.findById(userId).orElseThrow();

        List<UserSnsLink> userSnsLinks = new ArrayList<>();
        for(String link : snsLinks){
            userSnsLinks.add(UserSnsLink.addUserSnsLink(userInfo, link));
        }

        snsLinkRepository.saveAll(userSnsLinks);
    }
}
