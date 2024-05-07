package com.hana.login.common.config.oauth2.provider.impl

import com.hana.login.common.domain.en.Gender
import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
class NaverUserInfo(
    private val attributes: Map<String, String>
) : Oauth2UserInfo {
    override fun getProviderId(): String {
        return attributes["id"].toString()
    }

    override fun getProvider(): String {
        return "naver"
    }

    override fun getEmail(): String {
        return attributes["email"].toString()
    }

    override fun getName(): String {
        return attributes["name"].toString()
    }

    override fun getGender(): Gender {
        return Gender.valueOf(attributes["gender"].toString())
    }

    override fun getPhoneNumber(): String? {
        return attributes["mobile"]
    }
}