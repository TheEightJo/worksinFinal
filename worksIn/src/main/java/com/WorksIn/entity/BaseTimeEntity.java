package com.WorksIn.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@EntityListeners(value = {AuditingEntityListener.class}) // Auditing을 하기위해 엔티티 리스너 추가
@MappedSuperclass // 부모 클래스를 상속받는 자식 클래스에 매핑정보만 제공
@Data
public abstract class BaseTimeEntity {

    @CreatedDate // 생성시 자동 저장
    @Column(updatable = false)
    private LocalDateTime regTime; // 등록일


    @LastModifiedDate // 변경시 자동 저장
    private LocalDateTime updateTime; // 수정일
}
