package com.hana.login.user.controller.request

data class UserLogin(
    val userId: String,
    val password: String,
) {
    companion object {
        fun fixture(
            userId: String = "hanana0627",
            password: String = "password",
        ) : UserLogin {
            return UserLogin(
                userId = userId,
                password = password,
            )
        }
    }
}
