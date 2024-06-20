package com.st.eighteen_be.user.service;

import com.st.eighteen_be.common.exception.ErrorCode;
import com.st.eighteen_be.common.exception.sub_exceptions.data_exceptions.NotValidException;
import com.st.eighteen_be.jwt.JwtTokenProvider;
import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserSnsLink;
import com.st.eighteen_be.user.dto.response.SnsLinksResponseDto;
import com.st.eighteen_be.user.repository.SnsLinkRepository;
import com.st.eighteen_be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<SnsLinksResponseDto> addSnsLink(HttpServletRequest request, List<String> snsLinks) {
        String requestAccessToken = jwtTokenProvider.resolveAccessToken(request);

        if (!jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new NotValidException(ErrorCode.ACCESS_TOKEN_NOT_VALID);
        }

        String certificationValue = jwtTokenProvider.getAuthentication(requestAccessToken).getName();

        UserInfo userInfo = userRepository.findByPhoneNumber(certificationValue).orElseThrow();

        List<UserSnsLink> userSnsLinks = new ArrayList<>();
        for(String link : snsLinks){
            userSnsLinks.add(UserSnsLink.addUserSnsLink(userInfo, link));
        }

        // DTO로 변환
        List<SnsLinksResponseDto> userSnsLinkDTOs = userSnsLinks.stream()
                .map(userSnsLink -> new SnsLinksResponseDto(userSnsLink.getLink()))
                .toList();

        snsLinkRepository.saveAll(userSnsLinks);

        return userSnsLinkDTOs;
    }


    public List<SnsLinksResponseDto> readAll(Integer userId){
        UserInfo userInfo = userRepository.findById(userId).orElseThrow();

        List<UserSnsLink> snsLinks = userInfo.getSnsLinks();
        List<SnsLinksResponseDto> snsLinksResponseDtoList = new ArrayList<>();
        for (UserSnsLink userSnsLink : snsLinks) {
            snsLinksResponseDtoList.add(new SnsLinksResponseDto(userSnsLink.getLink()));
        }

        return snsLinksResponseDtoList;
    }

}
