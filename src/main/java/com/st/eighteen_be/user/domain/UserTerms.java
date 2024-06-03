package com.st.eighteen_be.user.domain;

import com.st.eighteen_be.common.basetime.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class UserTerms extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terms_id")
    private Integer id;

    //@Column(columnDefinition = "TINYINT(1)") -> h2에서는 지원안함
    private Boolean a;

    //@Column(columnDefinition = "TINYINT(1)")
    private Boolean b;

    //@Column(columnDefinition = "TINYINT(1)")
    private Boolean c;

    //@Column(columnDefinition = "TINYINT(1)")
    private Boolean d;

    @Builder
    private UserTerms(Boolean a, Boolean b, Boolean c, Boolean d ){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

    }


}
