package com.hana.login.common.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class LoginLog(
    val userId: String,
    val timeStamp: LocalDateTime,
    val loginType: String, // -> login : LOGIN / logout : LOGOUT
    val userIp: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?
) {

    companion object {
        fun fixture(
            userId: String = "userName",
            timeStamp: LocalDateTime = LocalDateTime.now(),
            loginType: String = "SAMPLE",
            userIp: String = "127.0.0.1",
            id: Long? = null
        ): LoginLog {
            return LoginLog(
                userId = userId,
                timeStamp = timeStamp,
                loginType = loginType,
                userIp = userIp,
                id = id
            )
        }
    }
}