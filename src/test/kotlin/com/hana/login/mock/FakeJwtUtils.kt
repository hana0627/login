package com.hana.login.mock

import com.hana.login.common.utils.JwtUtils
import jakarta.servlet.http.HttpServletResponse

class FakeJwtUtils : JwtUtils{
    override fun generateToken(response: HttpServletResponse, memberId: String, memberName: String): String {
        return "Bearer tokenHeader.tokenPayload.tokenSignature"
    }

    override fun isExpired(token: String): Boolean {
        return false;
    }

    override fun getMemberId(token: String): String {
        return "hanana0627"
    }

    override fun isInValidated(token: String): Boolean {
        return false
    }

}