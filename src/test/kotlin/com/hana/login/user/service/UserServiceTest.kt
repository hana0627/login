package com.hana.login.user.service

import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import com.hana.login.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource


@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userCacheRepository: UserCacheRepository,
    private val userService: UserService,
    private val passwordEncoder: BCryptPasswordEncoder,
) {


    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll();
        userCacheRepository.flushAll()
    }

    @Test
    fun 회원가입이_정상_동작한다() {
        //given
        val dto: UserCreate = UserCreate.fixture();

        //when
        val id: Long = userService.join(dto)

        //then
        val result: UserEntity = userRepository.findById(id).get()

        assertThat(result).isNotNull
    }

    @Test
    fun 중복된_아이디로_회원가입이_불가능하다() {
        //given
        val entity: UserEntity = UserEntity.fixture()
        userRepository.save(entity)
        val dto: UserCreate = UserCreate.fixture();

        //when & then
        val result = assertThrows<ApplicationException> { userService.join(dto) }
        assertThat(result.errorCode).isEqualTo(ErrorCode.DUPLICATED_USER_ID)
        assertThat(result.message).isEqualTo("이미 가입된 회원입니다.")
    }


    @Test
    fun 올바른_정보_입력시_로그인이_성공하고_회원정보를_반환한다() {
        //given
        val entity: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01011112222",
            gender = Gender.F,
            )
        userRepository.save(entity)
        val dto: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )

        //when
        val result:UserEntity = userService.login(dto);

        //then
        assertThat(result.userId).isEqualTo("hanana0627")
        assertThat(result.userName).isEqualTo("박하나")
        assertThat(true).isEqualTo(passwordEncoder.matches("password", result.password))
        assertThat(result.phoneNumber).isEqualTo("01011112222")
        assertThat(result.gender).isEqualTo(Gender.F)
    }

    
    @Test
    fun 로그인시_아이디가_일치하지_않으면_예외를_생성한다() {
        //given
        val entity: UserEntity = UserEntity.fixture(password = passwordEncoder.encode("password"))
        userRepository.save(entity)
        val dto: UserLogin = UserLogin.fixture(userId = "wrongId")

        //when & then
        val result = assertThrows<ApplicationException> { userService.login(dto); }
        assertThat(result.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUNT)
        assertThat(result.message).isEqualTo("회원 정보가 없습니다.")
    }

    @Test
    fun 로그인시_비밀번호가_일치하지_않으면_예외를_생성한다() {
        //given
        val entity: UserEntity = UserEntity.fixture(password = passwordEncoder.encode("password"))
        userRepository.save(entity)
        val dto: UserLogin = UserLogin.fixture(password = "wrong_password")

        //when & then
        val result = assertThrows<ApplicationException> { userService.login(dto); }
        assertThat(result.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUNT)
        assertThat(result.message).isEqualTo("회원 정보가 없습니다.")
    }

    @Test
    fun 아이디_중복검증_성공시_true를_반환한다() {
        //given
        val entity: UserEntity = UserEntity.fixture(
            userId= "hanana",
            password = passwordEncoder.encode("password"))
        userRepository.save(entity)

        //when
        val result = userService.duplicateUser("success_id")

        //then
        assertThat(result).isTrue()
    }
    @Test
    fun 아이디_이미_존재하는_아이디이면_예외를_발생시킨다() {
        //given
        val entity: UserEntity = UserEntity.fixture(
            userId= "hanana",
            password = passwordEncoder.encode("password"))
        userRepository.save(entity)

        //when & then
        val result = assertThrows<ApplicationException> { userService.duplicateUser("hanana"); }
        assertThat(result.errorCode).isEqualTo(ErrorCode.DUPLICATED_USER_ID)
        assertThat(result.message).isEqualTo("이미 가입된 회원입니다.")
    }

    @Test
    fun 로그인이_성공한_후_서비스_요청시_회원이름_및_전화번호를_반환한다() {
        //given
        val entity: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = passwordEncoder.encode("password"),
            phoneNumber = "01012345678")

        userRepository.save(entity)
        userCacheRepository.setUser(entity)

        //when
        val result: UserInformation  = userService.getUserSimpleInformation(entity.userId)

        //then
        assertThat(result.userId).isEqualTo("hanana0627")
        assertThat(result.userName).isEqualTo("박하나")
        assertThat(result.phoneNumber).isEqualTo("01012345678")
    }

}



