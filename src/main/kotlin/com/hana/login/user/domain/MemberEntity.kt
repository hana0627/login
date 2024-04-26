package com.hana.login.user.domain

import com.hana.login.common.domain.AuditingFields
import jakarta.persistence.*


@Entity
@Table(name = "member_account")
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    @Column(length = 50, updatable = false)
    var memberId: String,
    @Column(length = 50)
    var userName: String,
    @Column(length = 50)
    var password: String,
    @Column(length = 50)
    var phoneNumber: String
) : AuditingFields() {
}
