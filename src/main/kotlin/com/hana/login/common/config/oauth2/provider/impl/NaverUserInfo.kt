package com.hana.login.common.config.oauth2.provider.impl

import com.hana.login.common.domain.en.Gender
import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
class NaverUserInfo(
    private val attributes: Map<String, Any>
) : Oauth2UserInfo {
    override fun getProviderId(): String {
        return attributes["id"] as String
    }

    override fun getProvider(): String {
        return "naver"
    }

    override fun getEmail(): String {
        return attributes["email"] as String
    }

    override fun getName(): String {
        return attributes["name"] as String
    }

    override fun getGender(): Gender {
        return Gender.valueOf(attributes["gender"].toString())
    }

    override fun getPhoneNumber(): String? {
        return attributes["mobile"] as String?
    }
}