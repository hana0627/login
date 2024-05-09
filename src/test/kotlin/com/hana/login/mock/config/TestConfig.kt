package com.hana.login.mock.config

import com.hana.login.common.repositroy.TokenRepository
import com.hana.login.common.repositroy.impl.TokenQueryRepository
import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.utils.FakeJwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig @Autowired constructor(
    private val tokenRepository: TokenRepository,
    private val tokenQueryRepository: TokenQueryRepository,
)
{

    @Bean
    fun jwtUtils(): JwtUtils {
        return FakeJwtUtils(tokenRepository, tokenQueryRepository)
    }
}
