package com.hana.login.common.domain

import com.hana.login.user.controller.request.MemberLogin
import jakarta.persistence.*
import java.util.*

@Entity
class Token (
    @Column(length = 50, updatable = false)
    @Id
    val memberId: String,
    @Column(updatable = false)
    val expiredAt: Date,
    @Column(length = 200, updatable = false)
    val refreshToken: String,
){
    companion object{
        fun fixture(
            memberId: String = "hanana9506",
            expiredAt: Date = Date(),
            refreshToken: String = "refreshToken"
        ) : Token {
            return Token(
                memberId,
                expiredAt,
                refreshToken,
            )
        }
    }

}
