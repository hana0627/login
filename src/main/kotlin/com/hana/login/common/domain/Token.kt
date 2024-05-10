package com.hana.login.common.domain

import jakarta.persistence.*
import java.util.*

@Entity
data class Token (
    @Column(length = 50, updatable = false)
    @Id
    val userId: String,
    @Column(updatable = false)
    val expiredAt: Date,
    @Column(length = 200, updatable = false)
    val refreshToken: String,
){
    companion object{
        fun fixture(
            userId: String = "hanana0627",
            expiredAt: Date = Date(),
            refreshToken: String = "refreshToken"
        ) : Token {
            return Token(
                userId,
                expiredAt,
                refreshToken,
            )
        }
    }

}
