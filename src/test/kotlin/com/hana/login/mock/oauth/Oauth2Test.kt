package com.hana.login.mock.oauth

import com.hana.login.common.config.oauth2.PrincipalOauth2UserService
import com.hana.login.common.config.oauth2.provider.impl.GoogleUserInfo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.properties")
class Oauth2Test @Autowired constructor(
    private val mvc: MockMvc,
    private val oauth2User: PrincipalOauth2UserService,
    private val principalOauth2UserService: PrincipalOauth2UserService,
) {


    @Test

    fun 구글_로그인_url_요청시_302_redirect가_진행된다() {
        //given

        //when & then
        mvc.perform(get("http://localhost:8080/oauth2/authorization/google"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", "https://accounts.google.com/o/oauth2/v2/auth"))
            .andExpect(redirectedUrl("https://accounts.google.com/o/oauth2/v2/auth"))
            .andDo(print())
    }

    @Test
    fun 구글_로그인_테스트() {
        //given
        mvc.perform(get("http://localhost:8080/login/oauth2/code/google")).andDo(print())
        //when

        //then


    }

}

//    @Test
//    fun `구글 로그인이 성공적으로 진행될 때 토큰 정보와 함께 302 Redirect가 진행된다`() {
//        // given
//
//        // OAuth2 테스트에 사용할 모의 AccessToken 설정
//        val accessToken = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test-token", null, null)
//
//        // 모의 사용자 정보 설정
//        val oauth2User = googleUser()
//
//
//        mvc.perform(
//            get("/login/oauth2/code/google").with(
//                oauth2Login().oauth2User(oauth2User)
//            )
//        )
//            .andExpect(status().is3xxRedirection)
//            .andExpect(redirectedUrl("http://localhost:3000/login?token=test-token"))
//    }
//
//    @Test
//    fun `구글 로그인이 성공적으로 진행될 때 토큰 정보와 함께 302 Redirect가 진행된다`() {
//        mvc.perform(
//            get("/login/oauth2/code/google").with(
//                // 모의 사용자 정보를 제공하여 OAuth2 로그인을 시뮬레이션
//                WithMockOAuth2User(
//                    authorities = ["ROLE_USER"],
//                    attributes = [
//                        "sub" to "google-subject",
//                        "name" to "John Doe",
//                        "email" to "john.doe@example.com",
//                        "email_verified" to true
//                    ],
//                    token = "test-token"
//                )
//            )
//        )
//            .andExpect(status().is3xxRedirection)
//            .andExpect(redirectedUrl("http://localhost:3000/login?token=test-token"))
//    }
//}
//
//
//object TestOAuth2Users {
//
//    fun googleUser(): OAuth2User {
//        val attributes: MutableMap<String, Any> = mutableMapOf(
//            "sub" to "google-subject",
//            "name" to "John Doe",
//            "email" to "john.doe@example.com",
//            // 다른 속성들도 필요에 따라 추가할 수 있습니다.
//        )
//
//        val authorities = AuthorityUtils.createAuthorityList("ROLE_USER")
//
//        val user = DefaultOAuth2User(authorities, attributes, "sub")
//        var accessToken = OAuth2AccessToken(
//            OAuth2AccessToken.TokenType.BEARER, "test-token",
//            Instant.now(), Instant.now().plusSeconds(3600)
//        )
//
//        return DefaultOAuth2User(authorities, attributes, "sub").apply {
//            attributes["name"] = "John Doe"
//            attributes["email"] = "john.doe@example.com"
//            attributes["sub"] = "google-subject"
//            attributes["email_verified"] = true
//            accessToken = accessToken
//        }
//    }
//}