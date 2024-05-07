package com.hana.login.mock.config

import com.hana.login.common.utils.JwtUtils
import com.hana.login.mock.FakeJwtUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestConfig {

    @Bean
    fun jwtUtils(): JwtUtils {
        return FakeJwtUtils()
    }
}