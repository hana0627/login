package com.hana.login.common.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.Token
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.TokenRepository
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
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
    private val memberRepository: MemberRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: BCryptPasswordEncoder,

    ) {

    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"
    private val expiredMs: Long = 1800
    private val refreshMs: Long = 5000

    @Test
    fun 유효한_정보로_토큰_재갱신_요청을_보내면_새로운_토큰을_발급한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        memberRepository.save(member)
        tokenRepository.save(Token.fixture(memberId = member.memberId, refreshToken = "Refresh$secretKey${member.memberId}$refreshMs"))

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .header(HttpHeaders.AUTHORIZATION,"Bearer $secretKey${member.memberId}${member.memberName}$expiredMs")
            .cookie(Cookie("refresh","Refresh$secretKey${member.memberId}$refreshMs"))
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Bearer new$secretKey${member.memberId}${member.memberName}$expiredMs"))
            .andDo(print())
    }


    @Test
    fun cookie에_refresh토큰_정보가_없으면_jwt토큰_갱신에_실패한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        memberRepository.save(member)
        tokenRepository.save(Token.fixture(memberId = member.memberId, refreshToken = "Refresh$secretKey${member.memberId}$refreshMs"))

        //when & then
        mvc.perform(get("/api/v2/regenerate")
            .cookie(Cookie("something","somethingValue"))
            .header(HttpHeaders.AUTHORIZATION,"Bearer $secretKey${member.memberId}${member.memberName}$expiredMs"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.TOKEN_NOT_FOUND.toString()))
            .andExpect(jsonPath("$.message").value("accessToken 혹은 refreshToken이 존재하지 않습니다."))
            .andDo(print())
    }


    @Test
    fun 토큰_정보_없이_토큰_재갱신_요청을_보내면_예외가_발생한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        memberRepository.save(member)

        //when & then
        mvc.perform(get("/api/v2/regenerate"))
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("jwt 토큰 정보가 없습니다"))
            .andDo(print())
    }


}


