package com.hana.login.common.config.oauth2

import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import com.hana.login.common.config.oauth2.provider.impl.GoogleUserInfo
import com.hana.login.common.config.oauth2.provider.impl.NaverUserInfo
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

//        println("===1===")
//        println(oauth2User)
        // 구글로그인
        //Name: [116839203346340279189], Granted Authorities: [[OAUTH2_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, SCOPE_openid]], User Attributes: [{sub=116839203346340279189, name=HANA PARK, given_name=HANA, family_name=PARK, picture=https://lh3.googleusercontent.com/a/ACg8ocKOEC-ufmo0GDKcBTchcLBl4ccZJ4ez-BF5uueSPAI5c3M6xy4=s96-c, email=hanana6270@gmail.com, email_verified=true, locale=ko}]
        // 네이버로그인
        //Name: [{id=4IMs4VgzQ7dekkWWRDYysDQs4fhyFP2KIKILCtEMPr4, gender=F, email=hanana9506@naver.com, mobile=010-3606-6270, mobile_e164=+821036066270, name=박하나, birthday=06-27}], Granted Authorities: [[OAUTH2_USER]], User Attributes: [{resultcode=00, message=success, response={id=4IMs4VgzQ7dekkWWRDYysDQs4fhyFP2KIKILCtEMPr4, gender=F, email=hanana9506@naver.com, mobile=010-3606-6270, mobile_e164=+821036066270, name=박하나, birthday=06-27}}]
//        println("===2===")
//        println(userRequest.clientRegistration.registrationId)
        // 구글로그인
        // google
        // 네이버로그인
        // naver

//        println("===3===")
//        oauth2User.attributes.forEach{print(it)}
        //구글로그인
        //sub=116839203346340279189 name=HANA PARK given_name=HANA family_name=PARK picture=https://lh3.googleusercontent.com/a/ACg8ocKOEC-ufmo0GDKcBTchcLBl4ccZJ4ez-BF5uueSPAI5c3M6xy4=s96-c email=hanana6270@gmail.com email_verified=true locale=ko
        //네이버로그인
        //resultcode=00 message=success response={id=4IMs4VgzQ7dekkWWRDYysDQs4fhyFP2KIKILCtEMPr4, gender=M, email=hanana9506@naver.com, mobile=010-3606-6270, mobile_e164=+821036066270, name=박하나, birthday=06-27}



        var oauth2UserInfo: Oauth2UserInfo? = null

        if("google" == userRequest.clientRegistration.registrationId) {
            oauth2UserInfo = GoogleUserInfo(oauth2User.attributes);
        }
        if("naver" == userRequest.clientRegistration.registrationId) {
            oauth2UserInfo = NaverUserInfo(oauth2User.attributes["response"] as Map<String, String>)
        }


        if(oauth2UserInfo == null) {
            throw ApplicationException(ErrorCode.UNSUPPORTED_PROVIDER, "지원하지 않는 로그인 타입입니다.")
        }


//        val provider: String = userRequest.clientRegistration.registrationId
        val provider: String = oauth2UserInfo.getName()
//        val providerId: String = oauth2User.getAttribute<String>("sub")!!
        val providerId: String = oauth2UserInfo.getProviderId()
//        val memberId: String = provider + "_" + providerId
        val memberId: String = provider + "_" + providerId

//        val userName: String = oauth2User.getAttribute<String>("name")!!
        val userName: String = oauth2UserInfo.getName()

        val phoneNumber: String = oauth2UserInfo.getPhoneNumber() ?: "010-0000-0000"
        val gender: Gender = oauth2UserInfo.getGender()

        val optionalMember:MemberEntity? = memberRepository.findByMemberId(memberId)
        val memberEntity: MemberEntity

        if(optionalMember == null) {
            memberEntity = MemberEntity(
                memberId = memberId,
                memberName = userName,
                password = "password",
                phoneNumber = phoneNumber,
                gender = gender,
                id = null,
            )
            memberRepository.save(memberEntity)
        } else {
            memberEntity = optionalMember
        }

        return CustomUserDetails(memberEntity, oauth2User.attributes)
    }
}
