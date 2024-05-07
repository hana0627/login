package com.hana.login.user.service

import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.utils.impl.JwtUtilsImpl
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class PrincipalOauth2UserService(
    private val memberRepository: MemberRepository,
): DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest?): CustomUserDetails {
        if(userRequest == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "userRequest is null")
        }

        val oauth2User: OAuth2User = super.loadUser(userRequest)


        val provider: String = userRequest.clientRegistration.registrationId
        val providerId: String = oauth2User.getAttribute<String>("sub")!!
        val memberId: String = provider + "_" + providerId
        val userName: String = oauth2User.getAttribute<String>("name")!!
        val optionalMember:MemberEntity? = memberRepository.findByMemberId(memberId)
        val memberEntity: MemberEntity

        if(optionalMember == null) {
            memberEntity = MemberEntity(
                memberId = memberId,
                memberName = userName,
                password = "password",
                phoneNumber = "01000000000",
                gender = Gender.UN,
                id = null,
            )
            memberRepository.save(memberEntity)
        } else {
            memberEntity = optionalMember
        }

        return CustomUserDetails(memberEntity, oauth2User.attributes)
    }
}
