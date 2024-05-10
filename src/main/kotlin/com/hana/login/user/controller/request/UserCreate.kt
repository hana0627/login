package com.hana.login.user.controller.request

import com.hana.login.common.domain.en.Gender

data class UserCreate(
    val userId: String,
    val userName: String,
    val password: String,
    val phoneNumber: String,
    val gender: Gender
) {

    companion object {
        fun fixture(
            userId: String = "hanana0627",
            userName: String = "박하나",
            password: String = "password", // TODO 암호화
            phoneNumber: String = "01012345678",
            gender: Gender = Gender.F
        ) : UserCreate {
            return UserCreate(
                userId = userId,
                userName = userName,
                password = password,
                phoneNumber = phoneNumber,
                gender = gender
            )
        }
    }
}
