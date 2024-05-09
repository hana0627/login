package com.hana.login.user.controller


import com.fasterxml.jackson.databind.ObjectMapper
import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.TokenRepository
import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberCacheRepository
import com.hana.login.user.repository.MemberRepository
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

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.properties")
class MemberControllerTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val mvc: MockMvc,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val memberRepository: MemberRepository,
    private val memberCacheRepository: MemberCacheRepository,
    private val tokenRepository: TokenRepository,

    ) {

    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"

    private val expiredMs: Long = 1800

    private val refreshMs: Long = 5000

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        memberCacheRepository.flushAll()
        tokenRepository.deleteAll()
    }


    @Test
    fun 아이디중복_검증시_중복된아이디가_없으면_true를_반환한다() {
        //given
        val entity: MemberEntity = MemberEntity.fixture(memberId = "hanana0627")
        memberRepository.save(entity)


        //when & then
        mvc.perform(get("/api/v1/duplicate/{memberId}", "hanana9999"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().string("true"))
            .andDo(print())
    }

    @Test
    fun 아이디중복_검증시_중복된아이디라면_예외를_발생한다() {
        //given
        val entity: MemberEntity = MemberEntity.fixture(memberId = "hanana0627")
        memberRepository.save(entity)

        //when & then
        mvc.perform(get("/api/v1/duplicate/{memberId}", "hanana0627"))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATED_MEMBER_ID.toString()))
            .andExpect(jsonPath("$.message").value("이미 가입된 회원입니다."))
            .andDo(print())
    }

    @Test
    fun 올바른_정보_입력시_회원가입이_성공한다() {
        //given
        val before: Long = memberRepository.count()
        val memberCreate = MemberCreate.fixture(
            memberId = "hanana0627",
            memberName = "박하나",
            password = "password",
            phoneNumber = "01011112222",
            gender = Gender.F,
        )

        val json = objectMapper.writeValueAsString(memberCreate)

        //when
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andDo(print())

        //then
        val member: MemberEntity = memberRepository.findByMemberId("hanana0627")!!
        assertThat(member.memberId).isEqualTo("hanana0627")
        assertThat(true).isEqualTo(passwordEncoder.matches("password", member.password))
        assertThat(member.phoneNumber).isEqualTo("01011112222")
        assertThat(member.gender).isEqualTo(Gender.F)
        val after: Long = memberRepository.count()
        assertThat(after).isEqualTo(before + 1)

    }

    @Test
    fun 회원가입시_중복된_아이디인_경우_예외를_발생한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627"
        )
        memberRepository.save(member)

        val memberCreate = MemberCreate.fixture(
            memberId = "hanana0627",
        )

        val json = objectMapper.writeValueAsString(memberCreate)

        //when & then
        mvc.perform(
            post("/api/v1/join")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATED_MEMBER_ID.toString()))
            .andExpect(jsonPath("$.message").value("이미 가입된 회원입니다."))
            .andDo(print())
    }

    @Test
    fun 올바른_정보를_입력하면_로그인이_성공하고_jwt_토큰을_응답으로_내리고_쿠키로_refresh_토큰을_반환한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        memberRepository.save(member)

        val memberLogin: MemberLogin = MemberLogin.fixture(
            memberId = "hanana0627",
            password = "password",
        )
        val json: String = objectMapper.writeValueAsString(memberLogin)

        //when & then
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().string("Bearer $secretKey${member.memberId}${member.memberName}$expiredMs"))
            .andExpect(cookie().value("refresh", "Refresh$secretKey${member.memberId}$refreshMs"))

    }

    @Test
    fun 로그인시_아이디나_패스워드를_잘못_입력하면_예외가_발생한다() {
        //given
        val member: MemberEntity = MemberEntity.fixture(
            memberId = "hanana0627",
            password = passwordEncoder.encode("password"),
        )
        memberRepository.save(member)

        val memberLogin: MemberLogin = MemberLogin.fixture(
            memberId = "wrongId",
            password = "password",
        )
        val json: String = objectMapper.writeValueAsString(memberLogin)

        //when & then
        mvc.perform(
            post("/api/v1/login")
                .contentType(APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.MEMBER_NOT_FOUNT.toString()))
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
    fun jwt토큰이_헤더에_있으면_인증이_필요한_요청이_성공한다() {
        //given
        val token: String = "Bearer tokenHeader.tokenPayload.tokenSignature"

        //when & then
        mvc.perform(get("/api/v2/auth").header("AUTHORIZATION", token))
            .andExpect(status().isOk)
            .andExpect(content().string("성공!"))
            .andDo(print())
    }

}
