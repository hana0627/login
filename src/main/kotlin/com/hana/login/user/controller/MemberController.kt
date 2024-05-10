package com.hana.login.user.controller

import com.hana.login.common.utils.JwtUtils
import com.hana.login.user.controller.request.MemberCreate
import com.hana.login.user.controller.request.MemberLogin
import com.hana.login.user.controller.response.MemberInformation
import com.hana.login.user.domain.MemberEntity
import com.hana.login.user.service.MemberService
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
class MemberController (
    private val memberService: MemberService,
    private val jwtUtils: JwtUtils,
){
    @GetMapping("/api/v1/duplicate/{memberId}")
    fun duplicateMember(@PathVariable memberId: String): ResponseEntity<Boolean> {
        val result:Boolean = memberService.duplicateMember(memberId);
        return ResponseEntity.ok(result)
    }

    @PostMapping("/api/v1/join")
    fun saveMember(@RequestBody requestDto: MemberCreate): ResponseEntity<Long> {
        val result: Long =  memberService.join(requestDto);

        return ResponseEntity.ok(result)
    }

    @PostMapping("/api/v1/login")
    fun login(@RequestBody requestDto: MemberLogin, response: HttpServletResponse): ResponseEntity<String> {

        val member: MemberEntity = memberService.login(requestDto)

        // 토큰생성
        val result =  jwtUtils.generateToken(
            response = response,
            memberId = member.memberId,
            memberName = member.memberName,
            phoneNumber = member.phoneNumber,
            password = member.password
        )

        return ResponseEntity.ok(result)
    }

    @GetMapping("/api/v2/auth")
    fun MyPage(
        authentication: Authentication
    ): ResponseEntity<MemberInformation> {
        val memberId: String = authentication.principal.toString()
        val result: MemberInformation = memberService.getUserSimpleInformation(memberId)
        return ResponseEntity.ok(result)
    }
}
