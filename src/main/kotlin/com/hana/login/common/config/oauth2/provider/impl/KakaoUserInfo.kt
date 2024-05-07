package com.hana.login.common.config.oauth2.provider.impl

import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import com.hana.login.common.domain.en.Gender
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
class KakaoUserInfo(
    private val attributes: MutableMap<String, Any>
) : Oauth2UserInfo {

    override fun getProviderId(): String {
        return attributes["id"].toString()
    }

    override fun getProvider(): String {
        return "kakao"
    }

    override fun getEmail(): String {
        val kakaoAccount = attributes["kakao_account"] as Map<String, String>?
        if (kakaoAccount != null && kakaoAccount.containsKey("has_email") && kakaoAccount.containsKey("email_needs_agreement")) {

            // 이메일을 가지고 있고
            val hasEmail = kakaoAccount["has_email"] as Boolean
            // 이메일
            val emailNeedsAgreement = kakaoAccount["email_needs_agreement"] as Boolean
            if (hasEmail && emailNeedsAgreement) {
                return kakaoAccount["email"].toString()
            }
        }
        return "a"
    }

    override fun getName(): String {
        //이름정보가 없어서 nickname 정보 반환


        return "a"
    }

    override fun getGender(): Gender {
        //성별정보

        return Gender.UN
    }

    override fun getPhoneNumber(): String? {
        //TODO

        return "a"
    }
}