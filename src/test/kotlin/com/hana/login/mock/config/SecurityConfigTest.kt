//package com.hana.login.mock.config
//import com.hana.login.common.config.oauth2.PrincipalOauth2UserService
//import com.hana.login.common.domain.CustomUserDetails
//import com.hana.login.common.utils.JwtUtils
//import com.hana.login.user.service.MemberService
//import org.junit.jupiter.api.Test
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.`when`
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
//import org.springframework.boot.test.mock.mockito.MockBean
//import org.springframework.context.annotation.Import
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
//import org.springframework.security.oauth2.core.user.OAuth2User
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
//
//@WebMvcTest
//@Import(MemberService::class) // 추가: MemberService 로드
//class SecurityConfigTest {
//
//    @Autowired
//    lateinit var mockMvc: MockMvc
//
//    @MockBean
//    lateinit var clientRegistrationRepository: ClientRegistrationRepository
//
//    @MockBean
//    lateinit var principalOauth2UserService: PrincipalOauth2UserService
//
//    @MockBean
//    lateinit var jwtUtils: JwtUtils
//
//    @Test
//    fun `OAuth2 로그인 후 리다이렉트 테스트`() {
//        // PrincipalOauth2UserService를 Mock으로 설정
//        val mockOAuth2User: CustomUserDetails = mock()
//        `when`(principalOauth2UserService.loadUser(any())).thenReturn(mockOAuth2User)
//
//        mockMvc.perform(get("/login/oauth2/code/google").with(oauth2Login()))
//            .andDo(print())
//            .andExpect(status().is3xxRedirection())
//        // 이후 리다이렉트된 페이지의 테스트를 추가할 수 있습니다.
//    }
//}