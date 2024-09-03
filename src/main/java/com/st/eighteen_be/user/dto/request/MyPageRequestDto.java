package com.st.eighteen_be.user.dto.request;

import com.st.eighteen_be.user.domain.UserQuestion;
import lombok.Builder;

import java.util.List;

public class MyPageRequestDto {

     private String mbti;

     private String introduction;

     private List<UserQuestion> questions;

     @Builder
     public MyPageRequestDto(String mbti, String introduction, List<UserQuestion> questions) {
         this.mbti = mbti;
         this.introduction = introduction;
         this.questions = questions;
     }
}
