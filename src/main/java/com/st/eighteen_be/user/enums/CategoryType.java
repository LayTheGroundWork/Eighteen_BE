package com.st.eighteen_be.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    BEAUTY("뷰티","BEAUTY"),
    SPORT("운동","SPORT"),
    STUDY("공부","STUDY"),
    ART("예술","ART"),
    GAME("게임","GAME"),
    ETC("기타","ETC");

    private static final Map<String,String> CATEGORIES_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(CategoryType::getCategory,CategoryType::name)));

    private final String category;
    private final String description;

    public static CategoryType of(final String category) {
        return CategoryType.valueOf(CATEGORIES_MAP.get(category));
    }
}
