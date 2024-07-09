package com.hana.login.doc

import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.config.TestConfig
import com.hana.login.mock.config.TestSecurityConfig
import com.hana.login.user.controller.UserController
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@MockBean(JpaMetamodelMappingContext::class) // JPA 불러오기
@WebMvcTest(UserController::class) // 테스트할 코드
@Import(TestSecurityConfig::class, TestConfig::class)
@TestPropertySource("classpath:test-application.properties")
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.hanalogin.com", uriPort = 443)
@DisplayName("[RESTDocs] CourseControllerDocTest")
class UserControllerDocTest(
//    private val objectMapper: ObjectMapper,
//    private val mvc: MockMvc,
//    private val passwordEncoder: BCryptPasswordEncoder,
//    private val userRepository: UserRepository,
//    private val userCacheRepository: UserCacheRepository,
//    private val tokenCacheRepository: TokenCacheRepository,
//    private val loginLogRepository: LoginLogRepository,
    ) {
//    @BeforeEach
//    fun beforeEach() {
//        userRepository.deleteAll()
//        userCacheRepository.flushAll()
//    }

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var om: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var jwtUtils: JwtUtils


    @Test
    fun 아이디_중복체크() {
        //given
        val userId = "userId"
        val json = om.writeValueAsString(userId)
        given(userService.duplicateUser(userId)).willReturn(true)

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/duplicate/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value(true))
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

        then(userService).should().duplicateUser(userId)
    }

    @Test
    fun 아이디_중복체크_중복된_아이디() {
        //given
        val userId = "userId"
        val json = om.writeValueAsString(userId)

        given(userService.duplicateUser(userId)).willThrow(ApplicationException(ErrorCode.DUPLICATED_USER_ID, "이미 가입된 회원입니다."))

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/duplicate/{userId}", userId)
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict) // 중복된 아이디로 예상
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.DUPLICATED_USER_ID.toString()))
            .andExpect(jsonPath("$.error.message").value("이미 가입된 회원입니다."))
            .andDo(print())
            .andDo(
                document(
                    "duplicateUser_error",
                    pathParameters(
                        parameterWithName("userId").description("사용자아이디")
                    )
                )
            )

        then(userService).should().duplicateUser(userId)
    }

    @Test
    fun 회원가입() {
        //given
        val dto: UserCreate = UserCreate.fixture()
        val json = om.writeValueAsString(dto)

        given(userService.join(dto)).willReturn(1L)

        //when & then
        mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value(1))
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

        then(userService).should().join(dto)
    }


    @Test
    fun 로그인() {
        //given
        val dto: UserLogin = UserLogin.fixture(userId = "userId", password = "password")
        val user = UserEntity.fixture(userId = dto.userId)
        val json = om.writeValueAsString(dto)

        given(userService.login(dto)).willReturn(user)

        //when && then
        mvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
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

        then(userService).should().login(dto)
    }


    @Test
    fun 마이페이지_인증필요() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01012345678")

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        val userInfo = UserInformation(
            userId = user.userId,
            userName = user.userName,
            phoneNumber = user.phoneNumber
        )

        given(userService.getUserSimpleInformation(anyString())).willReturn(userInfo)

        //when && them
        mvc.perform(MockMvcRequestBuilders.get("/api/v2/auth")
            .header(HttpHeaders.AUTHORIZATION, token)
            .with(user(CustomUserDetails(user, mutableMapOf())))) // TODO 어노테이션 적용하기
            .andExpect(status().isOk)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result.userId").value("hanana0627"))
            .andExpect(jsonPath("$.result.userName").value("박하나"))
            .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
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

        then(userService).should().getUserSimpleInformation(user.userId)

    }


    @Test
    fun 로그아웃_인증필요() {
        //given
        val user = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01012345678"
        )

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when && then
        mvc.perform(MockMvcRequestBuilders.get("/api/v2/logout")
            .header(HttpHeaders.AUTHORIZATION, token)
            .with(user(CustomUserDetails(user, mutableMapOf())))) // TODO 어노테이션 적용하기)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                            .andExpect(jsonPath("$.result").value("true"))
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