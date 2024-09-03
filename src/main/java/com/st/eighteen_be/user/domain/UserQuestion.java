package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.user.enums.QuestionsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_question", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","question"}, name = "USER_QUESTION_UNIQUE")
})
public class UserQuestion extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionsType question;

    @Column(nullable = false)
    private String answer;

    public UserQuestion(UserInfo user, QuestionsType question, String answer) {
        this.question = question;
        this.answer = answer;
        this.user = setUser(user);
    }

    public UserInfo setUser(UserInfo user) {
        this.user = user;
        user.getUserQuestions().add(this);

        return user;
    }
}
