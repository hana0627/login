package com.hana.login.user.controller.request

data class MemberLogin(
    val memberId: String,
    val password: String,
) {
    companion object {
        fun fixture(
            memberId: String = "hanana0627",
            password: String = "password",
        ) : MemberLogin {
            return MemberLogin(
                memberId = memberId,
                password = password,
            )
        }
    }
}
