package com.hana.login.common.controller

import com.hana.login.common.controller.response.Response
import com.hana.login.common.utils.JwtUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class TokenController(
    private val jwtUtils: JwtUtils,
) {


    @GetMapping("/api/v2/regenerate")
    fun renewToken(request: HttpServletRequest,
                   response : HttpServletResponse,
                   @RequestHeader("Authorization") authorization: String,
                   ): Response<String> {

        val cookies = request.cookies
        var refreshToken: String? = null

        for (cookie in cookies) {
            if(cookie.name == "refresh") {
                refreshToken = cookie.value
            }
        }

        val accessToken: String = authorization.split(" ")[1].trim()

        return Response.success(jwtUtils.reGenerateToken(response, accessToken, refreshToken))
    }

}
