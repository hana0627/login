package com.hana.login.common.config

import com.hana.login.common.config.filter.JwtFiler
import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.utils.JwtUtils
import com.hana.login.common.config.oauth2.PrincipalOauth2UserService
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig(
    private val jwtUtils: JwtUtils,
    private val principalOauth2UserService: PrincipalOauth2UserService,
) {
    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { csrf -> csrf.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/oauth2/**").permitAll()
                    .requestMatchers("/login/**").permitAll()
                    .requestMatchers("/api/v1/**").permitAll()
                    .requestMatchers("/api/v2/**").authenticated()
            }

            .formLogin { form -> form.disable() }

            .httpBasic { b -> b.disable() }

            //oauth2 로그인
            .oauth2Login { o ->
                o.loginPage("http://localhost:3000/login") // 권한 없을시
                    o.userInfoEndpoint { userInfoEndpoint ->
                        userInfoEndpoint.userService(principalOauth2UserService)
                    }
                    .successHandler { _, response, authentication ->
                        val principal: CustomUserDetails = authentication.principal as CustomUserDetails
                        val jwtToken = jwtUtils.generateToken(response, principal.name, principal.getMemberName())
                        response.sendRedirect("http://localhost:3000/login?token=$jwtToken")
                    }
            }

            .addFilterBefore(
                JwtFiler(secretKey = secretKey, jwtUtils = jwtUtils),
                UsernamePasswordAuthenticationFilter::class.java
            )

            .build()
    }
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { it.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations().toString(), "/favicon.ico") }
    }
}
