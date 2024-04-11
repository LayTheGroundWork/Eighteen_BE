package com.st.eighteen_be.member.domain;

import com.st.eighteen_be.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MemberTerms extends BaseTimeEntity {

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
    private MemberTerms(Boolean a, Boolean b, Boolean c, Boolean d ){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

    }


}
