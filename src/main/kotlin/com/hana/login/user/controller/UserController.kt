package com.hana.login.user.controller

import com.hana.login.common.controller.response.Response
import com.hana.login.common.utils.JwtUtils
import com.hana.login.user.controller.request.UserCreate
import com.hana.login.user.controller.request.UserLogin
import com.hana.login.user.controller.response.UserInformation
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class UserController (
    private val userService: UserService,
    private val jwtUtils: JwtUtils,
){
    @GetMapping("/api/v1/duplicate/{userId}")
    fun duplicateUser(@PathVariable userId: String): Response<Boolean> {
        val result:Boolean = userService.duplicateUser(userId);
        return Response.success(result)
    }

    @PostMapping("/api/v1/join")
    fun saveUser(@RequestBody requestDto: UserCreate): Response<Long> {
        val result: Long =  userService.join(requestDto);

        return Response.success(result)
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody requestDto: UserLogin, request: HttpServletRequest, response: HttpServletResponse): Response<String> {

        val user: UserEntity = userService.login(requestDto)

        // 토큰생성
        val result =  jwtUtils.generateToken(
            request= request,
            response = response,
            userId = user.userId,
            userName = user.userName,
            phoneNumber = user.phoneNumber,
            password = user.password
        )
        return Response.success(result)
    }

    @GetMapping("/api/v2/auth")
    fun MyPage(
        authentication: Authentication
    ): Response<UserInformation> {
        val userId: String = authentication.principal.toString()
        val result: UserInformation = userService.getUserSimpleInformation(userId)
        return Response.success(result)
    }

    @GetMapping("/api/v2/logout")
    fun logout(
        request: HttpServletRequest,
        authentication: Authentication): Response<Boolean> {
        val userId: String = authentication.principal.toString()
        val result = jwtUtils.logout(request, userId)
        return Response.success(result)
    }
}
