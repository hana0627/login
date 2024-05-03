package com.hana.login.common.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.exception.en.ErrorCode
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.properties")
class TokenControllerTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mvc: MockMvc,

    ) {

    fun 유효한_정보로_토큰_재갱신_요청을_보내면_새로운_토큰을_발급한다() {
        //given

        //when
        mvc.perform(get("/api/v2/regenerate")
            .header("Authentication","Bearer tokenHeader.tokenPayload.tokenSignature")
            .cookie())
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.DUPLICATED_MEMBER_ID.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미 가입된 회원입니다."))
            .andDo(MockMvcResultHandlers.print())

        //then

    }
}
