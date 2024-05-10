package com.hana.login.common.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.RefreshToken
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import com.hana.login.user.repository.UserRepository
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.properties")
class TokenControllerTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mvc: MockMvc,
    private val userRepository: UserRepository,
    private val userCacheRepository: UserCacheRepository,
    private val tokenCacheRepository: TokenCacheRepository,
    private val passwordEncoder: BCryptPasswordEncoder,

    ) {

    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"
    private val expiredMs: Long = 1800
    private val refreshMs: Long = 5000

    @Test
    fun 유효한_정보로_토큰_재갱신_요청을_보내면_새로운_토큰을_발급한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userRepository.save(user)
        userCacheRepository.setUser(user)
        tokenCacheRepository.setToken(RefreshToken.fixture(userId = user.userId, refreshToken = "Refresh$secretKey${user.userId}$refreshMs"))

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .header(HttpHeaders.AUTHORIZATION,"Bearer $secretKey${user.userId}${user.userName}$expiredMs")
            .cookie(Cookie("refresh","Refresh$secretKey${user.userId}$refreshMs"))
        )
//            .andExpect(status().isOk)
//            .andExpect(content().string("Bearer new$secretKey${user.userId}${user.userName}$expiredMs"))
            .andDo(print())
    }


    @Test
    fun cookie에_refresh토큰_정보가_없으면_jwt토큰_갱신에_실패한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userRepository.save(user)
        tokenCacheRepository.setToken(RefreshToken.fixture(userId = user.userId, refreshToken = "Refresh$secretKey${user.userId}$refreshMs"))

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .cookie(Cookie("something","somethingValue"))
            .header(HttpHeaders.AUTHORIZATION,"Bearer $secretKey${user.userId}${user.userName}$expiredMs"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.TOKEN_NOT_FOUND.toString()))
            .andExpect(jsonPath("$.error.message").value("accessToken 혹은 refreshToken이 존재하지 않습니다."))
            .andDo(print())
    }


    @Test
    fun 토큰_정보_없이_토큰_재갱신_요청을_보내면_예외가_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userRepository.save(user)

        //when & then
        mvc.perform(get("/api/v2/regenerate"))
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("jwt 토큰 정보가 없습니다"))
            .andDo(print())
    }


}


