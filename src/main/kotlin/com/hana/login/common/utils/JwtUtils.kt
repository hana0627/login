package com.hana.login.common.utils


interface JwtUtils {
    fun generateToken(memberId: String, memberName: String,): String
    fun isExpired(token: String): Boolean
    fun getMemberId(token: String): String
    fun isInValidated(token: String): Boolean

}