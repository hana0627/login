package com.hana.login.mock.config

import com.hana.login.common.repositroy.LoginLogRepository
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.utils.FakeJwtUtils
import com.hana.login.user.repository.UserCacheRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig @Autowired constructor(
    private val tokenCacheRepository: TokenCacheRepository,
    private val userCacheRepository: UserCacheRepository,
    private val loginLogRepository: LoginLogRepository,
)
{

    @Bean
    fun jwtUtils(): JwtUtils {
        return FakeJwtUtils(tokenCacheRepository, userCacheRepository, loginLogRepository)
    }
}
