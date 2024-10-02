package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.user.enums.QuestionsType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserQuestion extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionsType question;

    @Column(nullable = false)
    private String answer;

    @Builder
    public UserQuestion(QuestionsType question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        user.addQuestionAndAnswer(this);
    }
}
