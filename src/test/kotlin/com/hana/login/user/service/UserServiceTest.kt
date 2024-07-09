package com.hana.login.user.service

import com.hana.login.common.domain.en.Gender
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@ExtendWith(MockitoExtension::class)
class UserServiceTest (
    @Mock private val userRepository: UserRepository,
    @Mock private val userCacheRepository: com.hana.login.user.repository.UserCacheRepository,
    @Mock private val passwordEncoder: BCryptPasswordEncoder,

    ) {
    @InjectMocks
    private val userService: UserService =  UserService(userCacheRepository, userRepository, passwordEncoder)

    @Test
    fun 회원가입이_정상_동작한다() {
        //given
        val dto: UserCreate = UserCreate.fixture();
        val userEntity = UserEntity.fixture(password = "EncryptedPassword")

        given(passwordEncoder.encode(dto.password)).willReturn("EncryptedPassword")
        given(userRepository.save(userEntity))
            .willReturn(UserEntity.fixture(
                id = 1,
                password = "EncryptedPassword")
            )

        //when
        val result: Long = userService.join(dto)

        //then
        then(userRepository).should().save(userEntity)

        assertThat(result).isEqualTo(1L)
    }

    @Test
    fun 중복된_아이디로_회원가입이_불가능하다() {
        //given
        val dto: UserCreate = UserCreate.fixture(userId = "hanana")
        val existingUser = UserEntity.fixture(userId = "hanana")

        given(userRepository.findByUserId(dto.userId)).willReturn(existingUser)

        //when & then
        val result = assertThrows<ApplicationException> { userService.join(dto) }

        then(userRepository).should().findByUserId(dto.userId)

        assertThat(result.errorCode).isEqualTo(ErrorCode.DUPLICATED_USER_ID)
        assertThat(result.message).isEqualTo("이미 가입된 회원입니다.")
    }


    @Test
    fun 올바른_정보_입력시_로그인이_성공하고_회원정보를_반환한다_캐시가_있는_경우() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01011112222",
            gender = Gender.F,
            id = 1
        )
        val dto: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )

        given(passwordEncoder.encode("password")).willReturn("EncryptedPassword")
        given(passwordEncoder.matches("password","EncryptedPassword")).willReturn(true)
        given(userCacheRepository.findByUserId(dto.userId)).willReturn(user)

        //when
        val result:UserEntity = userService.login(dto);

        //then
        then(userCacheRepository).should().findByUserId(dto.userId)
        then(userRepository).should(never()).findByUserId(anyString())

        assertThat(result.userId).isEqualTo("hanana0627")
        assertThat(result.userName).isEqualTo("박하나")
        assertThat(true).isEqualTo(passwordEncoder.matches("password", result.password))
        assertThat(result.phoneNumber).isEqualTo("01011112222")
        assertThat(result.gender).isEqualTo(Gender.F)
    }


    @Test
    fun 올바른_정보_입력시_로그인이_성공하고_회원정보를_반환한다_캐시가_없는_경우() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId = "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01011112222",
            gender = Gender.F,
            id = 1
        )
        val dto: UserLogin = UserLogin.fixture(
            userId = "hanana0627",
            password = "password",
        )


        given(passwordEncoder.encode("password")).willReturn("EncryptedPassword")
        given(passwordEncoder.matches("password","EncryptedPassword")).willReturn(true)
        given(userCacheRepository.findByUserId(dto.userId)).willReturn(null)
        given(userRepository.findByUserId(dto.userId)).willReturn(user)

        //when
        val result:UserEntity = userService.login(dto);

        //then
        then(userCacheRepository).should().findByUserId(dto.userId)
        then(userRepository).should().findByUserId(anyString())

        assertThat(result.userId).isEqualTo("hanana0627")
        assertThat(result.userName).isEqualTo("박하나")
        assertThat(true).isEqualTo(passwordEncoder.matches("password", result.password))
        assertThat(result.phoneNumber).isEqualTo("01011112222")
        assertThat(result.gender).isEqualTo(Gender.F)
    }



    @Test
    fun 로그인시_아이디가_일치하지_않으면_예외를_생성한다() {
        //given
        val dto: UserLogin = UserLogin.fixture(userId = "wrongId")
        given(userCacheRepository.findByUserId(dto.userId)).willReturn(null)
        given(userRepository.findByUserId(dto.userId)).willReturn(null)

        //when & then
        val result = assertThrows<ApplicationException> { userService.login(dto); }

        then(userCacheRepository).should().findByUserId(dto.userId)
        then(userRepository).should().findByUserId(dto.userId)

        assertThat(result.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUNT)
        assertThat(result.message).isEqualTo("회원 정보가 없습니다.")
    }

    @Test
    fun 로그인시_비밀번호가_일치하지_않으면_예외를_생성한다() {
        //given
        val user: UserEntity = UserEntity.fixture(userId = "hanana0627", password = "EncryptedPassword")
        val dto: UserLogin = UserLogin.fixture(userId = "hanana0627", password = "wrong_password")
        given(userCacheRepository.findByUserId(dto.userId)).willReturn(user)
        given(passwordEncoder.matches(dto.password, user.password)).willReturn(false)

        //when & then
        val result = assertThrows<ApplicationException> { userService.login(dto); }

        then(userCacheRepository).should().findByUserId(dto.userId)
        then(userRepository).should(never()).findByUserId(anyString())
        then(passwordEncoder).should().matches(any(),any())

        assertThat(result.errorCode).isEqualTo(ErrorCode.USER_NOT_FOUNT)
        assertThat(result.message).isEqualTo("회원 정보가 없습니다.")
    }

    @Test
    fun 아이디_중복검증_성공시_true를_반환한다() {
        //given
        given(userRepository.findByUserId("success_id")).willReturn(null)

        //when
        val result = userService.duplicateUser("success_id")

        //then
        then(userRepository).should().findByUserId("success_id")

        assertThat(result).isTrue()
    }

    @Test
    fun 아이디_이미_존재하는_아이디이면_예외를_발생시킨다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            password = "EncryptedPassword")

        given(userRepository.findByUserId("hanana0627")).willReturn(user)

        //when & then
        val result = assertThrows<ApplicationException> { userService.duplicateUser("hanana0627"); }

        then(userRepository).should().findByUserId("hanana0627")

        assertThat(result.errorCode).isEqualTo(ErrorCode.DUPLICATED_USER_ID)
        assertThat(result.message).isEqualTo("이미 가입된 회원입니다.")
    }

    @Test
    fun 로그인이_성공한_후_서비스_요청시_회원이름_및_전화번호를_반환한다() {
        //given
        val user: UserEntity = UserEntity.fixture(
            userId= "hanana0627",
            userName = "박하나",
            password = "EncryptedPassword",
            phoneNumber = "01012345678")

        given(userCacheRepository.findByUserId(user.userId)).willReturn(user)

        //when
        val result: UserInformation  = userService.getUserSimpleInformation(user.userId)

        //then
        then(userCacheRepository).should().findByUserId(user.userId)
        then(userRepository).should(never()).findByUserId(anyString())

        assertThat(result.userId).isEqualTo("hanana0627")
        assertThat(result.userName).isEqualTo("박하나")
        assertThat(result.phoneNumber).isEqualTo("01012345678")
    }

}
