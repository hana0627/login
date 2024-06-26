package com.hana.login.common.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


interface JwtUtils {
    fun generateToken(request: HttpServletRequest, response: HttpServletResponse, userId: String, userName: String, phoneNumber:String, password: String): String
    fun isExpired(token: String): Boolean
    fun getUserId(token: String): String
    fun isInValidated(token: String): Boolean
    fun reGenerateToken(response: HttpServletResponse, accessToken: String, refreshToken: String?): String
    fun logout(request: HttpServletRequest, userId: String): Boolean

}
