package com.hana.login.common.config.oauth2

import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserRepository
import com.hana.login.common.config.oauth2.provider.Oauth2UserInfo
import com.hana.login.common.config.oauth2.provider.impl.GoogleUserInfo
import com.hana.login.common.config.oauth2.provider.impl.KakaoUserInfo
import com.hana.login.common.config.oauth2.provider.impl.NaverUserInfo
import com.hana.login.user.repository.UserCacheRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class PrincipalOauth2UserService(
    private val userCacheRepository: UserCacheRepository,
    private val userRepository: UserRepository,
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
        // 카카오로그인
        // Name: [2775048014], Granted Authorities: [[OAUTH2_USER, SCOPE_account_email, SCOPE_birthday, SCOPE_gender, SCOPE_profile_nickname]], User Attributes: [{id=2775048014, connected_at=2023-05-04T08:00:31Z, properties={nickname=하나}, kakao_account={profile_nickname_needs_agreement=false, profile={nickname=하나, is_default_nickname=false}, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=shamoo1@naver.com, has_birthday=true, birthday_needs_agreement=false, birthday=0627, birthday_type=SOLAR, has_gender=true, gender_needs_agreement=false, gender=male}}]

//        println("===2===")
//        println(userRequest.clientRegistration.registrationId)
        // 구글로그인 -> google
        // 네이버 로그인 -> naver
        // 카카오 로그인 -> kakako

//        println("===3===")
//        oauth2User.attributes.forEach{print(it)}
        //구글로그인
        //sub=116839203346340279189 name=HANA PARK given_name=HANA family_name=PARK picture=https://lh3.googleusercontent.com/a/ACg8ocKOEC-ufmo0GDKcBTchcLBl4ccZJ4ez-BF5uueSPAI5c3M6xy4=s96-c email=hanana6270@gmail.com email_verified=true locale=ko
        //네이버로그인
        //resultcode=00 message=success response={id=4IMs4VgzQ7dekkWWRDYysDQs4fhyFP2KIKILCtEMPr4, gender=M, email=hanana9506@naver.com, mobile=010-3606-6270, mobile_e164=+821036066270, name=박하나, birthday=06-27}
        //카카오로그인
        //id=2775048014connected_at=2023-05-04T08:00:31Zproperties={nickname=하나}kakao_account={profile_nickname_needs_agreement=false, profile={nickname=하나, is_default_nickname=false}, has_email=true, email_needs_agreement=false, is_email_valid=true, is_email_verified=true, email=shamoo1@naver.com, has_birthday=true, birthday_needs_agreement=false, birthday=0627, birthday_type=SOLAR, has_gender=true, gender_needs_agreement=false, gender=male}Hibernate:

        var oauth2UserInfo: Oauth2UserInfo? = null

        if("google" == userRequest.clientRegistration.registrationId) {
            oauth2UserInfo = GoogleUserInfo(oauth2User.attributes)
        }
        if("naver" == userRequest.clientRegistration.registrationId) {
            oauth2UserInfo = NaverUserInfo(oauth2User.attributes["response"] as Map<String, Any?>)
        }
        if("kakao" == userRequest.clientRegistration.registrationId) {
            oauth2UserInfo = KakaoUserInfo(oauth2User.attributes)
        }

        if(oauth2UserInfo == null) {
            throw ApplicationException(ErrorCode.UNSUPPORTED_PROVIDER, "지원하지 않는 로그인 타입입니다.")
        }

        val userEntity: UserEntity = getUser(oauth2UserInfo)

        return CustomUserDetails(userEntity, oauth2User.attributes)
    }


    private fun getUser(oauth2UserInfo: Oauth2UserInfo): UserEntity {
        val provider: String = oauth2UserInfo.getProvider()
        val providerId: String = oauth2UserInfo.getProviderId()
        val userId: String = provider + "_" + providerId

        val userName: String = oauth2UserInfo.getName()

        val phoneNumber: String = oauth2UserInfo.getPhoneNumber() ?: "010-0000-0000"
        val gender: Gender = oauth2UserInfo.getGender()

        val optionalUser: UserEntity? = userCacheRepository.getUser(userId)?: userRepository.findByUserId(userId)
        val userEntity: UserEntity

        if (optionalUser == null) {
            userEntity = UserEntity(
                userId = userId,
                userName = userName,
                password = "password",
                phoneNumber = phoneNumber,
                gender = gender,
                id = null,
            )
            userRepository.save(userEntity)
        } else {
            userEntity = optionalUser
        }
        return userEntity
    }
}
