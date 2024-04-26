package com.hana.login.common.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@EntityListeners(AuditingEntityListener::class) // Entity 영속, 수정 이벤트 감지
@MappedSuperclass // 상속 및 공통필드 생성.
abstract class AuditingFields(
    @CreatedDate
    @Column(updatable = false)
    var createdDate: LocalDateTime? = null, // 생성일시

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null, // 생성자

    @LastModifiedDate
    var modifiedDate: LocalDateTime? = null, // 수정일시

    @LastModifiedBy
    var modifiedBy: String? = null, // 수정자
) {

}