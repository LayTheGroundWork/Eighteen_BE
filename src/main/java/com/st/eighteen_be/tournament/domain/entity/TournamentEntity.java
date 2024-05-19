package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import com.st.eighteen_be.common.converter.TournamentCategoryConverter;
import com.st.eighteen_be.tournament.domain.dto.response.TournamentSearchResponseDTO;
import com.st.eighteen_be.tournament.domain.enums.TournamentCategoryEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

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
    @Convert(converter = TournamentCategoryConverter.class)
    @Column(name = "CATEGORY", nullable = false, length = 100)
    private TournamentCategoryEnums category;

    @Lob
    @Builder.Default
    @Column(name = "THUMBNAIL_URL")
    private String thumbnailUrl = THUMBNAIL_DEFAULT_URL;

    public static TournamentEntity createTournamentEntity(TournamentCategoryEnums category) {
        return TournamentEntity.builder()
                .category(category)
                .build();
    }

    public TournamentSearchResponseDTO toTournamentSearchResponseDTO() {
        return TournamentSearchResponseDTO.builder()
                .tournamentNo(tournamentNo)
                .tournamentThumbnailUrl(thumbnailUrl)
                .build();
    }
}