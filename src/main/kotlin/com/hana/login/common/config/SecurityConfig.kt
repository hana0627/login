package com.hana.login.common.config

import com.hana.login.common.config.filter.JwtFiler
import com.hana.login.common.utils.JwtUtils
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry




@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig (
    private val jwtUtils: JwtUtils
){
    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }

            .authorizeHttpRequests { auth -> auth
                .requestMatchers("/api/v1/**").permitAll()
                .requestMatchers("/api/v2/**").authenticated()}

            .formLogin { form -> form.disable() }

            .httpBasic{ b -> b.disable()}

            .addFilterBefore(JwtFiler(secretKey = secretKey, jwtUtils = jwtUtils), UsernamePasswordAuthenticationFilter::class.java)

            .build()
    }
}
