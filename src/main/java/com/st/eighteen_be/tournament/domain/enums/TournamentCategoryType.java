package com.st.eighteen_be.tournament.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TournamentCategoryType {
    BEAUTY("뷰티","BEAUTY"),
    SPORT("운동","SPORT"),
    STUDY("공부","STUDY"),
    ART("예술","ART"),
    GAME("게임","GAME"),
    ETC("기타","ETC");

    private static final Map<String,String> CATEGORIES_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(TournamentCategoryType::getCategory, TournamentCategoryType::name)));

    private final String category;
    private final String description;

    public static TournamentCategoryType of(final String category) {
        if(category == null) {
            return null;
        }

        return TournamentCategoryType.valueOf(CATEGORIES_MAP.get(category));
    }
}
