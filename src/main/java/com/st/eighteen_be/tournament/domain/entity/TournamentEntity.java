package com.st.eighteen_be.tournament.domain.entity;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
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
    @Id
    @Comment("토너먼트고유번호")
    @Column(name = "TOURNAMENT_NO", nullable = false)
    private Long tournamentNo;
    
    @Size(max = 100)
    @NotNull
    @Comment("카테고리")
    @Column(name = "CATEGORY", nullable = false, length = 100)
    private String category;
}