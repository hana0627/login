package com.hana.login.mock.oauth

import com.hana.login.common.config.oauth2.PrincipalOauth2UserService
import org.hamcrest.core.StringContains.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class Oauth2Test @Autowired constructor(
    private val mvc: MockMvc,
) {

    @MockBean
    lateinit var principalOauth2UserService: PrincipalOauth2UserService


    @Test
    fun 구글_로그인_url_요청시_302_redirect가_진행된다() {
        //given
        //when & then
        mvc.perform(get("/oauth2/authorization/google"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", containsString("https://accounts.google.com/o/oauth2/v2/auth")))
            .andExpect(redirectedUrlPattern("https://accounts.google.com/o/oauth2/v2/**"))
            .andDo(print())
    }
    @Test
    fun 네이버_로그인_url_요청시_302_redirect가_진행된다() {
        //given
        //when & then
        mvc.perform(get("/oauth2/authorization/naver"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", containsString("https://nid.naver.com/oauth2.0/authorize")))
            .andExpect(redirectedUrlPattern("https://nid.naver.com/oauth2.0/**"))
            .andDo(print())
    }
    @Test
    fun 카카오_로그인_url_요청시_302_redirect가_진행된다() {
        //given

        //when & then
        mvc.perform(get("/oauth2/authorization/kakao"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", containsString("https://kauth.kakao.com/oauth/authorize")))
            .andExpect(redirectedUrlPattern("https://kauth.kakao.com/oauth/**"))
            .andDo(print())
    }



}