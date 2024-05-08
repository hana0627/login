package com.hana.login.common.config.oauth2.provider.impl

import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import com.hana.login.common.domain.en.Gender
import lombok.RequiredArgsConstructor


/**
 * 주 응답객체를 정확하게 다 확인해본게 아니라서
 * 실제 사용시에 키값을 변경해야 할 필요성 있음
 */
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
        val kakaoAccount = attributes["kakao_account"] as Map<*, *>

        if(!kakaoAccount.containsKey("has_email") || !kakaoAccount.containsKey("email_needs_agreement")) {
            return "email"
        }


        // 이메일을 가지고 있고
        val hasEmail = kakaoAccount["has_email"] as Boolean
        // 이메일
        val emailNeedsAgreement = kakaoAccount["email_needs_agreement"] as Boolean
        if (hasEmail && emailNeedsAgreement) {
            return kakaoAccount["email"] as String
        }
        return "email"
    }

    override fun getName(): String {

        //이름정보가 없어서 nickname 정보 반환
        val kakaoAccount = attributes["kakao_account"] as Map<*, *>

        val profile = kakaoAccount["profile"] as Map<*, *>
        return profile["nickname"] as String
    }

    override fun getGender(): Gender {

        val kakaoAccount = attributes["kakao_account"] as Map<*, *>

        if(!kakaoAccount.containsKey("has_gender") || !kakaoAccount.containsKey("gender_needs_agreement")) {
            return Gender.UN
        }


        val hasGender = kakaoAccount["has_gender"] as Boolean
        val genderNeedsAgreement = kakaoAccount["gender_needs_agreement"] as Boolean

        if (hasGender && genderNeedsAgreement) {
            return kakaoAccount["gender"] as Gender
        }

        return Gender.UN
    }

    override fun getPhoneNumber(): String? {

        val kakaoAccount = attributes["kakao_account"] as Map<*, *>

        if(!kakaoAccount.containsKey("has_phone_number") || !kakaoAccount.containsKey("phone_number_needs_agreement")) {
            return null
        }

        val hasPhoneNumber = kakaoAccount["has_phone_number"] as Boolean
        val phoneNumberNeedsAgreement = kakaoAccount["phone_number_needs_agreement"] as Boolean

        if (hasPhoneNumber && phoneNumberNeedsAgreement) {
            return kakaoAccount["phone_number"].toString()
        }

        return null
    }
}
