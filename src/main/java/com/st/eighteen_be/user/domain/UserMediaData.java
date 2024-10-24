package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMediaData extends BaseEntity {
    public static final String DEFAULT_IMAGE = "default";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_data_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    private String imageKey;

    private boolean isThumbnail;

    @Builder
    public UserMediaData(String imageKey, boolean isThumbnail){
        this.imageKey = imageKey;
        this.isThumbnail = isThumbnail;
    }

    public void setUser(UserInfo user){
        if(this.user != null)
            this.user.getMediaDataList().remove(this);

        this.user = user;
        user.getMediaDataList().add(this);
    }

    public void thumbnailFlagUpdate(){
        isThumbnail = !isThumbnail;
    }

    public boolean isDefaultImage(){
        return Objects.equals(imageKey, DEFAULT_IMAGE);
    }
}
