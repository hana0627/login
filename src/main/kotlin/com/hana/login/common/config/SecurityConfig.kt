package com.hana.login.common.config

import com.hana.login.common.config.filter.JwtFiler
import com.hana.login.common.domain.CustomUserDetails
import com.hana.login.common.utils.JwtUtils
import com.hana.login.user.service.PrincipalOauth2UserService
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
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
                o.loginPage("http://localhost:3000/login") // 권한 접근 실패 시 로그인 페이지로 이동
                o.defaultSuccessUrl("http://localhost:3000/myPage") // 로그인 성공 시 이동할 페이지
                .userInfoEndpoint { userInfoEndpoint ->
                            userInfoEndpoint.userService(principalOauth2UserService)

                }
                    .successHandler { _, response, authentication ->
                        val principal: CustomUserDetails = authentication.principal as CustomUserDetails
                        val jwtToken = jwtUtils.generateToken(response, principal.name, principal.getMemberName())
                        response.addHeader("Authorization", "Bearer $jwtToken")
                        //TODO redirectURL -> 서버응답으로 변경하여 로그인 시도하기??
                        response.sendRedirect("http://localhost:3000/myPage")
                    }

            }

            .addFilterBefore(
                JwtFiler(secretKey = secretKey, jwtUtils = jwtUtils),
                UsernamePasswordAuthenticationFilter::class.java
            )

            .build()
    }
}
