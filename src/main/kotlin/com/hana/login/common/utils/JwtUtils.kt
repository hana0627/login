package com.hana.login.common.utils

import jakarta.servlet.http.HttpServletResponse


interface JwtUtils {
    fun generateToken(response: HttpServletResponse, memberId: String, memberName: String, phoneNumber:String, password: String): String
    fun isExpired(token: String): Boolean
    fun getMemberId(token: String): String
    fun isInValidated(token: String): Boolean
    fun reGenerateToken(response: HttpServletResponse, accessToken: String, refreshToken: String?): String

}
