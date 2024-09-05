package com.st.eighteen_be.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionsType {
    ONE("첫 번째 질문"),
    TWO("두 번째 질문"),
    THREE("세 번째 질문"),
    FOUR("네 번째 질문"),
    FIVE("다섯 번째 질문"),
    SIX("여섯 번째 질문"),
    SEVEN("일곱 번째 질문"),
    EIGHT("여덟 번째 질문"),
    NINE("아홉 번째 질문"),
    TEN("열 번째 질문");

    private final String question;

}
