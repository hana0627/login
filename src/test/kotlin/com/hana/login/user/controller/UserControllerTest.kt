package com.hana.login.user.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.RefreshToken
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.LoginLogRepository
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import com.hana.login.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.properties")
class UserControllerTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mvc: MockMvc,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val userRepository: UserRepository,
    private val userCacheRepository: UserCacheRepository,
    private val tokenCacheRepository: TokenCacheRepository,
    private val loginLogRepository: LoginLogRepository,

    ) {

    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"

    private val expiredMs: Long = 1800

    private val refreshMs: Long = 5000

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
        userCacheRepository.flushAll()
        loginLogRepository.deleteAll()
    }


    @Test
    fun 아이디중복_검증시_중복된아이디가_없으면_true를_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture(userId = "hanana0627")
        userRepository.save(user)


        //when & then
        mvc.perform(get("/api/v1/duplicate/{userId}", "hanana9999"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("true"))
            .andDo(print())
    }

    @Test
    fun 아이디중복_검증시_중복된아이디라면_예외를_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture(userId = "hanana0627")
        userRepository.save(user)

        //when & then
        mvc.perform(get("/api/v1/duplicate/{userId}", "hanana0627"))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATED_USER_ID.toString()))
            .andExpect(jsonPath("$.message").value("이미 가입된 회원입니다."))
            .andDo(print())
    }

    @Test
    fun 올바른_정보_입력시_회원가입이_성공한다() {
        //given
        val before: Long = userRepository.count()
        val userCreate = UserCreate.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "password",
            phoneNumber = "01011112222",
            gender = Gender.F,
        )

        val json = objectMapper.writeValueAsString(userCreate)

        //when
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())

        //then
        val user: UserEntity = userRepository.findByUserId("hanana0627")!!
        assertThat(user.userId).isEqualTo("hanana0627")
        assertThat(true).isEqualTo(passwordEncoder.matches("password", user.password))
        assertThat(user.phoneNumber).isEqualTo("01011112222")
        assertThat(user.gender).isEqualTo(Gender.F)
        val after: Long = userRepository.count()
        assertThat(after).isEqualTo(before + 1)

    }

    @Test
    fun 회원가입시_중복된_아이디인_경우_예외를_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627"
        )
        userRepository.save(user)

        val userCreate = UserCreate.fixture(
            userId = "hanana0627",
        )

        val json = objectMapper.writeValueAsString(userCreate)

        //when & then
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATED_USER_ID.toString()))
            .andExpect(jsonPath("$.message").value("이미 가입된 회원입니다."))
            .andDo(print())
    }

    @Test
    fun 올바른_정보를_입력하면_로그인이_성공하고_jwt_토큰을_응답으로_내리고_쿠키로_refresh_토큰을_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userRepository.save(user)

        val userLogin: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )
        val json: String = objectMapper.writeValueAsString(userLogin)

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

    }

    @Test
    fun 로그인시_아이디나_패스워드를_잘못_입력하면_예외가_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userRepository.save(user)

        val userLogin: UserLogin = UserLogin.fixture(
            userId = "wrongId",
            password = "password",
        )
        val json: String = objectMapper.writeValueAsString(userLogin)

        //when & then
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.USER_NOT_FOUNT.toString()))
            .andExpect(jsonPath("$.message").value("회원 정보가 없습니다."))
            .andDo(print())
    }

    @Test
    fun jwt토큰이_없으면_인증이_필요한_요청이_불가능하다() {
        //given
        //nothing

        //when & then
        mvc.perform(get("/api/v2/auth"))
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("jwt 토큰 정보가 없습니다"))
            .andDo(print())

    }

    @Test
    fun 로그인이_성공하고_유효한_요청을_보내면_유저의_이름_및_전화번호를_반환한다_redis_캐시_없는경우도_성공() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(user)

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when & then
        mvc.perform(get("/api/v2/auth").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result.userId").value("hanana0627"))
            .andExpect(jsonPath("$.result.userName").value("박하나"))
            .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
            .andDo(print())
    }
    @Test
    fun 로그인이_성공하고_유효한_요청을_보내면_유저의_이름_및_전화번호를_반환한다_redis_캐시_사용하는_경우() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userCacheRepository.setUser(user)

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when & then
        mvc.perform(get("/api/v2/auth").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result.userId").value("hanana0627"))
            .andExpect(jsonPath("$.result.userName").value("박하나"))
            .andExpect(jsonPath("$.result.phoneNumber").value("01012345678"))
            .andDo(print())
    }

    @Test
    fun token_정보가_있으면_로그아웃이_성공적으로_이루어진다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(user)

        tokenCacheRepository.setToken(RefreshToken.fixture(
            userId = user.userId,
            expiredAt = Date(System.currentTimeMillis() + 4000 * 1000),
            refreshToken = "refreshToken"))

        val before: Boolean = tokenCacheRepository.getToken(user.userId) != null

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when
        mvc.perform(get("/api/v2/logout").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("true"))
            .andDo(print())

        //then
        val after: Boolean = tokenCacheRepository.getToken(user.userId) == null
        assertThat(before).isEqualTo(true)
        assertThat(after).isEqualTo(true)
    }

    @Test
    fun token_정보가_없으면_로그아웃시_예외가_발생한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(user)

        tokenCacheRepository.setToken(RefreshToken.fixture(
            userId = user.userId,
            expiredAt = Date(System.currentTimeMillis() + 4000 * 1000),
            refreshToken = "refreshToken"))

        //when then
        mvc.perform(get("/api/v2/logout"))
            .andExpect(status().isUnauthorized)
            .andExpect(status().reason("jwt 토큰 정보가 없습니다"))
            .andDo(print())

    }


    @Test
    fun 로그인_성공시_loginLog에_정보가_쌓인다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        userCacheRepository.setUser(user)

        val userLogin: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )
        val json: String = objectMapper.writeValueAsString(userLogin)

        val beforeCount: Long = loginLogRepository.count()


        //when
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json))

        //then
        val afterCount: Long = loginLogRepository.count()
        val result = loginLogRepository.findAll().get((afterCount-1).toInt())

        assertThat(beforeCount+1).isEqualTo(afterCount)
        assertThat(result.userId).isEqualTo(user.userId)
        assertThat(result.loginType).isEqualTo("LOGIN")
        assertThat(result.userIp).isEqualTo("127.0.0.1")
        //assertThat(result.timeStamp)....???.isBefore(now)???
    }

    @Test
    fun 로그아웃시_loginLog에_정보가_쌓인다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userCacheRepository.setUser(user)

        tokenCacheRepository.setToken(RefreshToken.fixture(
            userId = user.userId,
            expiredAt = Date(System.currentTimeMillis() + 4000 * 1000),
            refreshToken = "refreshToken")
        )

        val beforeCount: Long = loginLogRepository.count()

        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when
        mvc.perform(get("/api/v2/logout").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.result").value("true"))

        //then
        val afterCount: Long = loginLogRepository.count()
        val result = loginLogRepository.findAll().get((afterCount-1).toInt())

        assertThat(beforeCount+1).isEqualTo(afterCount)
        assertThat(result.userId).isEqualTo(user.userId)
        assertThat(result.loginType).isEqualTo("LOGOUT")
        assertThat(result.userIp).isEqualTo("127.0.0.1")
        //assertThat(result.timeStamp)....???.isBefore(now)???

    }
}
