package com.hana.login.user.controller.request

data class MemberLogin(
    val memberId: String,
    val password: String,
) {
    companion object {
        fun fixture(
            memberId: String = "hanana9506",
            password: String = "password", // TODO 암호화
        ) : MemberLogin {
            return MemberLogin(
                memberId = memberId,
                password = password,
            )
        }
    }
}
