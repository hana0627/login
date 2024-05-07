package com.hana.login.common.config.oauth2.provider.impl

import com.hana.login.common.domain.en.Gender
import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
class GoogleUserInfo(
    private val attributes: Map<String, Any>
) : Oauth2UserInfo {
    override fun getProviderId(): String {
        return attributes["sub"].toString()
    }

    override fun getProvider(): String {
        return "google"
    }

    override fun getEmail(): String {
        return attributes["email"].toString()
    }

    override fun getName(): String {
        return attributes["name"].toString()
    }

    override fun getGender(): Gender {
        return Gender.UN
    }

    override fun getPhoneNumber(): String? {
        return null
    }
}