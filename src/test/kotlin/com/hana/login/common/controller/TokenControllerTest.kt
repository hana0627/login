package com.hana.login.common.controller

import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.config.TestConfig
import com.hana.login.mock.config.TestSecurityConfig
import com.hana.login.user.domain.UserEntity
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpHeaders
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@MockBean(JpaMetamodelMappingContext::class) // JPA 불러오기
@WebMvcTest(TokenController::class) // 테스트할 코드
@AutoConfigureMockMvc(addFilters = false) // 테스트코드에서 필터사용 X
@Import(TestSecurityConfig::class,TestConfig::class) //이런식으로 사용하고 싶어
@TestPropertySource("classpath:test-application.properties")
class TokenControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    fun setUp() {
        val tokenController: TokenController = TokenController(jwtUtils)
    }

    private val secretKey: String = "testSecret.01234567890.testSecrettest0987654321"
    private val expiredMs: Long = 1800
    private val refreshMs: Long = 5000

    @Test
    @WithMockUser
    fun 유효한_정보로_토큰_재갱신_요청을_보내면_새로운_토큰을_발급한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
        )

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $secretKey${user.userId}${user.userName}$expiredMs")
            .cookie(Cookie("refresh", "Refresh $secretKey${user.userId}$refreshMs")))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("Bearer $secretKey${user.userId}${user.userName}$expiredMs"))
            .andDo(print())
    }


    @Test
    @WithMockUser
    fun cookie에_refresh토큰_정보가_없으면_jwt토큰_갱신에_실패한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
        )

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .cookie(Cookie("something","somethingValue"))
            .header(HttpHeaders.AUTHORIZATION,"Bearer $secretKey${user.userId}${user.userName}$expiredMs"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.TOKEN_NOT_FOUND.toString()))
            .andExpect(jsonPath("$.error.message").value("accessToken 혹은 refreshToken이 존재하지 않습니다."))
            .andDo(print())
    }


//    @Test
//    fun 토큰_정보_없이_토큰_재갱신_요청을_보내면_예외가_발생한다() {
//        //given
//        val user: UserEntity = UserEntity.fixture(
//            userId = "hanana0627",
//            password = "EncryptedPassword",
//        )
//
//        given(authentication.principal).willThrow(Exception())
//
//        //when & then
//        mvc.perform(get("/api/v2/regenerate"))
//            .andExpect(status().isUnauthorized)
//            .andExpect(status().reason("jwt 토큰 정보가 없습니다"))
//            .andDo(print())
//    }


}


