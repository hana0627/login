package com.hana.login.mock

import com.hana.login.common.utils.JwtUtils

class FakeJwtUtils : JwtUtils{
    override fun generateToken(memberId: String, memberName: String): String {
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