package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.user.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "TOURNAMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class TournamentEntity extends BaseEntity {
    public static final String THUMBNAIL_DEFAULT_URL = "https://ibb.co/f45yQ65";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("토너먼트고유번호")
    @Column(name = "TOURNAMENT_NO", nullable = false)
    private Long tournamentNo;

    @NotNull
    @Comment("카테고리")
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", nullable = false, length = 100)
    private CategoryType category;

    @Lob
    @Builder.Default
    @Column(name = "THUMBNAIL_URL")
    private String thumbnailUrl = THUMBNAIL_DEFAULT_URL;

    @Comment("토너먼트 시작일")
    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Comment("토너먼트 종료일")
    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Comment("토너먼트 시즌")
    @Column(name = "SEASON")
    private Integer season;

    @NotNull
    @Builder.Default
    @Comment("토너먼트 진행여부")
    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    public void endTournament() {
        this.status = false;
        this.endDate = LocalDateTime.now();
    }

    public static TournamentEntity createTournamentEntity(CategoryType category, int season) {
        return TournamentEntity.builder()
                .category(category)
                .startDate(LocalDateTime.now())
                .season(season)
                .status(true)
                .build();
    }

    public TournamentSearchResponseDTO toTournamentSearchResponseDTO() {
        return TournamentSearchResponseDTO.builder()
                .tournamentNo(tournamentNo)
                .thumbnailUrl(thumbnailUrl)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
