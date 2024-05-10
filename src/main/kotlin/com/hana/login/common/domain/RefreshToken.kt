package com.hana.login.common.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash(value = "Refresh")
data class RefreshToken (
    @Id
    val userId: String,
    val expiredAt: Date,
    val refreshToken: String,
){
    constructor(): this("",Date(System.currentTimeMillis() + 4000 * 1000),"")
    companion object{
        fun fixture(
            userId: String = "hanana0627",
            expiredAt: Date = Date(System.currentTimeMillis() + 4000 * 1000),
            refreshToken: String = "refreshToken"
        ) : RefreshToken {
            return RefreshToken(
                userId,
                expiredAt,
                refreshToken,
            )
        }
    }

}
