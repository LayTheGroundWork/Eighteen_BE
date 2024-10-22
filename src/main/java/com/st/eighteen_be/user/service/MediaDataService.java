package com.st.eighteen_be.user.service;

import com.st.eighteen_be.user.domain.UserInfo;
import com.st.eighteen_be.user.domain.UserMediaData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MediaDataService {

    @Transactional
    public void addMediaData(UserInfo user, List<String> keys){
        for(String imageKey : keys){
            UserMediaData userMediaData = UserMediaData.builder()
                    .imageKey(imageKey)
                    .isThumbnail(false)
                    .build();

            userMediaData.setUser(user);
        }
    }
}
