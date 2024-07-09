package com.hana.login.mock.config

import com.hana.login.common.domain.RefreshToken
import com.hana.login.common.repositroy.LoginLogRepository
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.utils.FakeJwtUtils
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig {
    @MockBean
    private lateinit var tokenCacheRepository: TokenCacheRepository

    @MockBean
    private lateinit var userCacheRepository: UserCacheRepository

    @MockBean
    private lateinit var loginLogRepository: LoginLogRepository
    @Bean
    fun jwtUtils(): JwtUtils {
        given(userCacheRepository.findByUserId(anyString())).willReturn(UserEntity.fixture())
        given(tokenCacheRepository.getToken(anyString())).willReturn(RefreshToken.fixture())
        return FakeJwtUtils(tokenCacheRepository, userCacheRepository, loginLogRepository)
    }
}
