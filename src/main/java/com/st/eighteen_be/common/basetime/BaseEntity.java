package com.st.eighteen_be.common.basetime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class BaseEntity {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;
}