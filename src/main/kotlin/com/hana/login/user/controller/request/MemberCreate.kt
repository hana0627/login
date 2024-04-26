package com.hana.login.user.controller.request

data class MemberCreate(
    val memberId: String,
    val memberName: String,
    val password: String,
    val phoneNumber: String
) {

    companion object {
        fun fixture(
            memberId: String = "hanana9506",
            memberName: String = "박하나",
            password: String = "password", // TODO 암호화
            phoneNumber: String = "01012345678"
        ) : MemberCreate {
            return MemberCreate(
                memberId = memberId,
                memberName = memberName,
                password = password,
                phoneNumber = phoneNumber
            )
        }
    }
}
