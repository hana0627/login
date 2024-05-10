package com.hana.login.user.controller.request

import com.hana.login.common.domain.en.Gender

data class MemberCreate(
    val memberId: String,
    val memberName: String,
    val password: String,
    val phoneNumber: String,
    val gender: Gender
) {

    companion object {
        fun fixture(
            memberId: String = "hanana0627",
            memberName: String = "박하나",
            password: String = "password", // TODO 암호화
            phoneNumber: String = "01012345678",
            gender: Gender = Gender.F
        ) : MemberCreate {
            return MemberCreate(
                memberId = memberId,
                memberName = memberName,
                password = password,
                phoneNumber = phoneNumber,
                gender = gender
            )
        }
    }
}
