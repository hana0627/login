package com.hana.login.user.domain

import com.hana.login.common.domain.AuditingFields
import com.hana.login.common.domain.en.Gender
import jakarta.persistence.*


@Entity
@Table(name = "member_account")
data class MemberEntity(
    @Column(length = 50, updatable = false)
    var memberId: String,
    @Column(length = 50)
    var memberName: String,
    @Column(length = 200)
    var password: String,
    @Column(length = 50)
    var phoneNumber: String,
    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    val gender: Gender,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
) : AuditingFields() {

    companion object {
        fun fixture(
           memberId: String = "hanana9506",
           memberName: String = "박하나",
           password: String = "password",
           phoneNumber: String = "01012345678",
           gender: Gender = Gender.F,
           id: Long? = null
        ) : MemberEntity {
            return MemberEntity(
                memberId = memberId,
                memberName = memberName,
                password = password,
                phoneNumber = phoneNumber,
                gender = gender,
                id= id)
        }
    }

}
