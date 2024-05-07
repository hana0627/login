package com.hana.login.common.config.oauth2.provider

import com.hana.login.common.domain.en.Gender

interface Oauth2UserInfo {
    fun getProviderId(): String
    fun getProvider(): String
    fun getEmail(): String
    fun getName(): String

    fun getGender(): Gender
    fun getPhoneNumber(): String?

}