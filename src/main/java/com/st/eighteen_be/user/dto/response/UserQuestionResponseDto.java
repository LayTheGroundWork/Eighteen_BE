package com.st.eighteen_be.user.dto.response;
import com.st.eighteen_be.user.domain.UserQuestion;
import com.st.eighteen_be.user.enums.QuestionsType;
import lombok.Getter;

@Getter
public class UserQuestionResponseDto {

    private final Integer id;

    private final QuestionsType question;

    private final String answer;

    public UserQuestionResponseDto(UserQuestion entity) {
        this.id = entity.getId();
        this.question = entity.getQuestion();
        this.answer = entity.getAnswer();
    }
}