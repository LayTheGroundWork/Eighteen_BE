package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotFoundException;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserSnsLink;
import com.st.eighteen_be.user.repository.SnsLinkRepository;
import com.st.eighteen_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SnsLinkService {

    private final UserRepository userRepository;
    private final SnsLinkRepository snsLinkRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public List<String> addSnsLink(@RequestHeader("Authorization") String accessToken,
                                                List<String> snsLinks) {
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(accessToken);

        if (!jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }

        String uniqueId = jwtTokenProvider.getAuthentication(requestAccessToken).getName();

        UserInfo userInfo = userRepository.findByUniqueId(uniqueId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );

        List<UserSnsLink> userSnsLinkList = new ArrayList<>();
        for(String link : snsLinks){
            UserSnsLink userSnsLink = UserSnsLink.builder()
                    .link(link)
                    .user(userInfo)
                    .build();

            userSnsLinkList.add(userSnsLink);
            userInfo.addSnsLink(userSnsLink);
        }

        snsLinkRepository.saveAll(userSnsLinkList);

        return snsLinks;
    }


    public List<String> readAll(Integer userId){
        UserInfo userInfo = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_USER)
        );

        List<UserSnsLink> snsLinks = userInfo.getSnsLinks();
        List<String> snsLinkList = new ArrayList<>();

        for (UserSnsLink userSnsLink : snsLinks) {
            snsLinkList.add(userSnsLink.getLink());
        }

        return snsLinkList;
    }

}
