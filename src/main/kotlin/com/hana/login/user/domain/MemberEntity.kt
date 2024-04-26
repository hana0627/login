package com.hana.login.user.domain

import com.hana.login.common.domain.AuditingFields
import com.hana.login.user.controller.request.MemberCreate
import jakarta.persistence.*


@Entity
@Table(name = "member_account")
data class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    @Column(length = 50, updatable = false)
    var memberId: String,
    @Column(length = 50)
    var memberName: String,
    @Column(length = 50)
    var password: String,
    @Column(length = 50)
    var phoneNumber: String
) : AuditingFields() {








    companion object {
        fun fixture(
           memberId: String = "hanana9506",
           memberName: String = "박하나",
           password: String = "password", // TODO 암호화
           phoneNumber: String = "01012345678",
           id: Long? = null
        ) : MemberEntity {
            return MemberEntity(
                memberId = memberId,
                memberName = memberName,
                password = password,
                phoneNumber = phoneNumber,
                id= id)
        }

        fun of(dto: MemberCreate): MemberEntity {
            return fixture(
                memberId = dto.memberId,
                memberName = dto.memberName,
                password = dto.password,
                phoneNumber = dto.phoneNumber
            )
        }


    }

}
