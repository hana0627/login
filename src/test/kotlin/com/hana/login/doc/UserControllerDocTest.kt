package com.hana.login.doc

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.RefreshToken
import com.hana.login.common.repositroy.LoginLogRepository
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import com.hana.login.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*


@SpringBootTest
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.hanalogin.com", uriPort = 443)
@DisplayName("[RESTDocs] CourseControllerDocTest")
@TestPropertySource("classpath:test-application.properties")
class UserControllerDocTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mvc: MockMvc,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    private val userCacheRepository: UserCacheRepository,
    private val tokenCacheRepository: TokenCacheRepository,
    private val loginLogRepository: LoginLogRepository,

    ) {
    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
        userCacheRepository.flushAll()
    }

    @Test
    fun 아이디_중복체크() {
        //given
        val userId = "userId"
        val json = objectMapper.writeValueAsString(userId)

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/duplicate/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andDo(
                document(
                    "duplicateUser",
                    pathParameters(
                        parameterWithName("userId").description("사용자아이디")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").description("결과"),
                        fieldWithPath("result").description("사용자아이디")
                    ),
                )
            )
    }

    @Test
    fun 아이디_중복체크_중복된_아이디() {
        //given
        val userId = "userId"
        val json = objectMapper.writeValueAsString(userId)

        // 중복 체크를 먼저 수행
        mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/duplicate/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk) // 중복되지 않은 아이디로 예상

        userRepository.save(UserEntity.fixture(userId = "userId"))

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/duplicate/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict) // 중복된 아이디로 예상
            .andDo(print())
            .andDo(
                document(
                    "duplicateUser_error",
                    pathParameters(
                        parameterWithName("userId").description("사용자아이디")
                    )
                )
            )
    }

    @Test
    fun 회원가입() {
        //given
        val dto: UserCreate = UserCreate.fixture()
        val json = objectMapper.writeValueAsString(dto)

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andDo(
                document(
                    "createUser", requestFields(
                        fieldWithPath("userId").description("사용자아이디"),
                        fieldWithPath("userName").description("사용자이름"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("phoneNumber").description("핸드폰번호"),
                        fieldWithPath("gender").description("성별")
                    ),
                    responseFields(
                        fieldWithPath("resultCode").description("사용자아이디"),
                        fieldWithPath("result").description("사용자아이디")
                    )
                )
            )
    }


    @Test
    fun 로그인() {
        //given
        val dto: UserLogin = UserLogin.fixture(userId = "userId", password = "password")
        userCacheRepository.setUser(UserEntity.fixture(userId = dto.userId, password = passwordEncoder.encode(dto.password)))
        val json = objectMapper.writeValueAsString(dto)

        //when && then
        mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andDo(
                document(
                    "login",
                    requestFields(
                        fieldWithPath("userId").description("사용자아이디"),
                        fieldWithPath("password").description("비밀번호"),
                    ),
                    responseFields(
                        fieldWithPath("resultCode").description("결과"),
                        fieldWithPath("result").description("jwt토큰")
                    ),
                )
            )
    }


    @Test
    fun 마이페이지_인증필요() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(user)

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when && them
        mvc.perform(MockMvcRequestBuilders.get("/api/v2/auth").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.result.userId").value("hanana0627"))
//            .andExpect(jsonPath("$.result.userName").value("박하나"))
//            .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
            .andDo(print())
            .andDo(
                document(
                    "myPage",
                    responseFields(
                        fieldWithPath("resultCode").description("결과"),
                        fieldWithPath("result.userId").description("사용자아이디"),
                        fieldWithPath("result.userName").description("사용자이름"),
                        fieldWithPath("result.phoneNumber").description("핸드폰번호"),
                    ),
                )
            )

    }


    @Test
    fun 로그아웃_인증필요() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(user)

        tokenCacheRepository.setToken(
            RefreshToken.fixture(
            userId = user.userId,
            expiredAt = Date(System.currentTimeMillis() + 4000 * 1000),
            refreshToken = "refreshToken"))

        val before: Boolean = tokenCacheRepository.getToken(user.userId) != null

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when && then
        mvc.perform(MockMvcRequestBuilders.get("/api/v2/logout").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andDo(print())
            .andDo(
                document(
                    "logout",
                    responseFields(
                        fieldWithPath("resultCode").description("결과"),
                        fieldWithPath("result").description("로그아웃 성공여부")
                    ),
                )
            )

    }


}