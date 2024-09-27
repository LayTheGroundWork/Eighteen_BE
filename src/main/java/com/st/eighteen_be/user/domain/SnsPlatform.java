package com.st.eighteen_be.user.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnsPlatform {

    private String instagram;
    private String x;
    private String tiktok;
    private String youtube;

    @Builder
    public SnsPlatform(String instagram, String x, String tiktok, String youtube){
        this.instagram = instagram;
        this.x = x;
        this.tiktok = tiktok;
        this.youtube = youtube;
    }
}
