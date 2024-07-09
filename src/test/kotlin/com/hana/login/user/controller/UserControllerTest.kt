package com.hana.login.user.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.config.TestConfig
import com.hana.login.mock.config.TestSecurityConfig
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@MockBean(JpaMetamodelMappingContext::class) // JPA 불러오기
@WebMvcTest(UserController::class) // 테스트할 코드
@Import(TestSecurityConfig::class,TestConfig::class)
@TestPropertySource("classpath:test-application.properties")
class UserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var jwtUtils: JwtUtils

    @Autowired
    private lateinit var om: ObjectMapper


    private val secretKey: String = "testSecret.01234567890.testSecrettest0987654321"

    private val expiredMs: Long = 1800

    private val refreshMs: Long = 5000


    fun setUp() {
        val userController: UserController = UserController(userService, jwtUtils)
    }

    @Test
    fun 아이디중복_검증시_중복된아이디가_없으면_true를_반환한다() {
        //given
        given(userService.duplicateUser(anyString())).willReturn(true)

        //when & then
        mvc.perform(get("/api/v1/duplicate/{userId}", "hanana9999"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value(true))
            .andDo(print())

        then(userService).should().duplicateUser("hanana9999")
    }

    @Test
    fun 아이디중복_검증시_중복된아이디라면_예외를_발생한다() {
        //given
        // TODO 프로덕션 코드가 달라지더라도 이 테스트는 성공한다.
        // 그러나, UserService 단위테스트가 실패할 것이므로 조금 찝찝한 느낌이 들어도 이렇게 작성하였다.
        given(userService.duplicateUser("hanana0627")).willThrow(
            ApplicationException(ErrorCode.DUPLICATED_USER_ID, "이미 가입된 회원입니다.")
        )

        //when & then
        mvc.perform(get("/api/v1/duplicate/{userId}", "hanana0627"))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.DUPLICATED_USER_ID.toString()))
            .andExpect(jsonPath("$.error.message").value("이미 가입된 회원입니다."))
            .andDo(print())

        then(userService).should().duplicateUser("hanana0627")
    }

    @Test
    fun 올바른_정보_입력시_회원가입이_성공한다() {
        //given
//        val before: Long = userRepository.count()
        val userCreate = UserCreate.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "password",
            phoneNumber = "01011112222",
            gender = Gender.F,
        )

        given(userService.join(userCreate)).willReturn(1L)

        val json = om.writeValueAsString(userCreate)
        //when
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value(1))
            .andDo(print())

        //then
        then(userService).should().join(userCreate)
    }

    @Test
    fun 회원가입시_중복된_아이디인_경우_예외를_발생한다() {
        //given
        val userCreate = UserCreate.fixture(
            userId = "hanana0627",
        )

        val json = om.writeValueAsString(userCreate)

        given(userService.join(userCreate)).willThrow(
            ApplicationException(
                ErrorCode.DUPLICATED_USER_ID,
                "이미 가입된 회원입니다."
            )
        )


        //when & then
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.DUPLICATED_USER_ID.toString()))
            .andExpect(jsonPath("$.error.message").value("이미 가입된 회원입니다."))
            .andDo(print())
    }

    @Test
    fun 올바른_정보를_입력하면_로그인이_성공하고_jwt_토큰을_응답으로_내리고_쿠키로_refresh_토큰을_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = "EncryptedPassword",
        )

        val userLogin: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )
        val json: String = om.writeValueAsString(userLogin)

        given(userService.login(userLogin)).willReturn(user)

        //when & then
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("Bearer $secretKey${user.userId}${user.userName}$expiredMs"))
            .andExpect(cookie().value("refresh", "Refresh$secretKey${user.userId}$refreshMs"))

        then(userService).should().login(userLogin)

    }

    @Test
    fun 로그인시_아이디나_패스워드를_잘못_입력하면_예외가_발생한다() {
        //given
        val userLogin: UserLogin = UserLogin.fixture(
            userId = "wrongId",
            password = "password",
        )
        val json: String = om.writeValueAsString(userLogin)

        given(userService.login(userLogin)).willThrow(ApplicationException(ErrorCode.USER_NOT_FOUNT, "회원 정보가 없습니다."))

        //when & then
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error.errorCode").value(ErrorCode.USER_NOT_FOUNT.toString()))
            .andExpect(jsonPath("$.error.message").value("회원 정보가 없습니다."))
            .andDo(print())

        then(userService).should().login(userLogin)
    }

    @Test
    fun 로그인이_성공하고_유효한_요청을_보내면_유저의_이름_및_전화번호를_반환한다() {
        // given
        val user = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01012345678"
        )
        val token = "Bearer tokenHeader.tokenPayload.tokenSignature"
        val userInfo = UserInformation(
            userId = user.userId,
            userName = user.userName,
            phoneNumber = user.phoneNumber
        )

        given(userService.getUserSimpleInformation(anyString())).willReturn(userInfo)

        // when & then
        mvc.perform(get("/api/v2/auth")
            .header(HttpHeaders.AUTHORIZATION, token)
            .with(user(CustomUserDetails(user, mutableMapOf())))) // TODO 어노테이션 적용하기
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result.userId").value("hanana0627"))
            .andExpect(jsonPath("$.result.userName").value("박하나"))
            .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
            .andDo(print())

        then(userService).should().getUserSimpleInformation("hanana0627")
    }

    @Test
    fun token_정보가_있으면_로그아웃이_성공적으로_이루어진다() {
        //given
        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"
        val user = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01012345678"
        )

        //when
        mvc.perform(get("/api/v2/logout")
            .header(HttpHeaders.AUTHORIZATION, token)
            .with(user(CustomUserDetails(user, mutableMapOf())))) // TODO 어노테이션 적용하기)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("true"))
            .andDo(print())
    }

}



// 삽질기록
//@Test
//fun 로그인이_성공하고_유효한_요청을_보내면_유저의_이름_및_전화번호를_반환한다() {
//    // given
//    val user = UserEntity.fixture(
//        userId = "hanana0627",
//        userName = "박하나",
//        password = "EncryptedPassword",
//        phoneNumber = "01012345678"
//    )
//    val token = "Bearer tokenHeader.tokenPayload.tokenSignature"
//    val userInfo = UserInformation(
//        userId = user.userId,
//        userName = user.userName,
//        phoneNumber = user.phoneNumber
//    )
//
//    given(userService.getUserSimpleInformation(anyString())).willReturn(userInfo)
//
//    // Mocking Authentication and SecurityContext
////        val authentication = mock(Authentication::class.java)
////        given(authentication.principal).willReturn(user.userId)
////        val securityContext = mock(SecurityContext::class.java)
////        given(securityContext.authentication).willReturn(authentication)
////        SecurityContextHolder.setContext(securityContext)
//
//    // when & then
//    mvc.perform(get("/api/v2/auth")
//        .header(HttpHeaders.AUTHORIZATION, token)
//        .with(user(CustomUserDetails(user, mutableMapOf())))) // TODO 어노테이션 적용하기
//        .andExpect(status().isOk)
//        .andExpect(jsonPath("$.result.userId").value("hanana0627"))
//        .andExpect(jsonPath("$.result.userName").value("박하나"))
//        .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
//        .andDo(print())
//
//    then(userService).should().getUserSimpleInformation("hanana0627")
//}