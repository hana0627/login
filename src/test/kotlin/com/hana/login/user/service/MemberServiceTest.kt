package com.hana.login.user.service

import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource


@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class MemberServiceTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val memberService: MemberService) {

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll();
    }

    @Test
    fun 회원가입이_정상_동작한다() {
        //given
        val dto: MemberCreate = MemberCreate.fixture();

        //when
        val id: Long = memberService.join(dto)

        //then
        val result: MemberEntity = memberRepository.findById(id).get()

        assertThat(result).isNotNull
    }

    @Test
    fun 중복된_아이디로_회원가입이_불가능하다() {
        //given
        val entity: MemberEntity = MemberEntity.fixture()
        memberRepository.save(entity)
        val dto: MemberCreate = MemberCreate.fixture();

        //when & then
        val result = assertThrows<ApplicationException> { memberService.join(dto) }
        assertThat(result.errorCode).isEqualTo(ErrorCode.DUPLICATED_MEMBER_ID)
        assertThat(result.message).isEqualTo("이미 가입된 회원입니다.")
    }


    @Test
    fun 올바른_정보_입력시_로그인이_성공한다() {
        //given
        val entity: MemberEntity = MemberEntity.fixture()
        memberRepository.save(entity)
        val dto: MemberLogin = MemberLogin.fixture()

        //when
        memberService.login(dto);

        //then
        //nothing
    }
    @Test
    fun 아이디_혹은_패스워드가_일치하지_않을_시_예외를_생성한다() {
        //given
        val entity: MemberEntity = MemberEntity.fixture()
        memberRepository.save(entity)
        val dto: MemberLogin = MemberLogin.fixture(memberId = "wrongId")

        //when & then
        val result = assertThrows<ApplicationException> { memberService.login(dto); }
        assertThat(result.errorCode).isEqualTo(ErrorCode.MEMBER_NOT_FOUNT)
        assertThat(result.message).isEqualTo("회원 정보가 없습니다.")


    }
}


